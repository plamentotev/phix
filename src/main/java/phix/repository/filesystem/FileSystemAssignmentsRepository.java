/*
Copyright (c) 2017 Faculty of Mathematics and Informatics - Sofia University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package phix.repository.filesystem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import phix.assignment.AssignmentNotFoundException;
import phix.assignment.InvalidAssignmentException;
import phix.assignment.AssignmentsRepository;
import phix.repository.PhixRepositoryException;
import phix.assignment.Assignment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemAssignmentsRepository implements AssignmentsRepository {

    private static final Log log = LogFactory.getLog(FileSystemAssignmentsRepository.class);

    private static final String MANIFEST_FILE_NAME = "manifest.yml";

    private final Path basePath;

    private final AssignmentParser assignmentParser;

    public FileSystemAssignmentsRepository(String baseDirectory,
                                           AssignmentParser assignmentParser) {

        this.basePath = Paths.get(baseDirectory).normalize();
        this.assignmentParser = assignmentParser;
    }

    @Override
    public List<Assignment> getAllAssignments() throws PhixRepositoryException {
        try (Stream<Path> assignmentDirs = Files.find(basePath, 1, this::isAssignmentDir)) {

            return assignmentDirs
                    .map(this::parseAssignmentOptional)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.warn("IO error occurred while trying to read the assignments from " + basePath.toAbsolutePath(), e);

            throw new PhixRepositoryException("IO error occurred while trying to read the assignments", e);
        }
    }

    @Override
    public Assignment getAssignment(String id) throws PhixRepositoryException {
        Path assignmentDir = basePath.resolve(id).normalize();

        if (!assignmentDir.startsWith(basePath)) {
            log.error("Trying to read from outside assignment directory. Trying to read from " + assignmentDir);

            throw new PhixRepositoryException("Invalid assignment directory.");
        }

        if (!Files.exists(assignmentDir)) {
            throw new AssignmentNotFoundException(String.format("No assignment with id: %s found.", id));
        }

        try {
            if (!isAssignmentDir(assignmentDir)) {
                log.warn(assignmentDir + " does not contains assignment.");

                throw new InvalidAssignmentException(String.format("Assignment with id %s is not a valid assignment.",
                        id));
            }

            return parseAssignment(assignmentDir);
        } catch (IOException e) {
            log.warn(String.format("IO error occurred while trying to read assignment with id %s from %s",
                    id, assignmentDir), e);

            throw new PhixRepositoryException("IO error occurred while trying to read assignment with id: " +
                    id, e);
        } catch (AssignmentParserException e) {
            log.warn("Exception occurred while trying to read assignment with id " + id, e);

            throw new InvalidAssignmentException(String.format("Assignment with id %s is not a valid assignment.",
                    id));
        } catch (InvalidAssignmentException e) {
            log.warn("Exception occurred while trying to read assignment with id " + id, e);

            throw e;
        }
    }

    private Optional<Assignment> parseAssignmentOptional(Path assignmentDir) {
        try {

            return Optional.of(parseAssignment(assignmentDir));

        } catch (IOException | PhixRepositoryException e) {
            log.warn("Exception occurred while trying to read the assignments from " + assignmentDir + ". Skipping.", e);

            return Optional.empty();
        }
    }

    /**
     *
     * @param assignmentDir
     * @return
     * @throws IOException
     * @throws AssignmentParserException
     * @throws PhixRepositoryException
     */
    private Assignment parseAssignment(Path assignmentDir) throws IOException, PhixRepositoryException {
        try (InputStream manifest = Files.newInputStream(assignmentDir.resolve(MANIFEST_FILE_NAME))) {
            Assignment assignment = assignmentParser.parseAssignment(manifest);

            String assignmentDirName = assignmentDir.getFileName().toString();
            if (!assignmentDirName.equals(assignment.getId())) {
                log.warn("Assignment with id: " + assignment.getId() + " should be placed in directory with the same " +
                        "name as the id. It's located in " + assignmentDirName);

                throw new InvalidAssignmentException("Assignment should be placed in directory with the same " +
                        "name as the assignment id.");
            }

            return assignment;
        }
    }

    private boolean isAssignmentDir(Path path) throws IOException {
        BasicFileAttributes fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);

        return isAssignmentDir(path, fileAttributes);
    }

    private boolean isAssignmentDir(Path path, BasicFileAttributes basicFileAttributes) {
        if (!basicFileAttributes.isDirectory()) {
            return false;
        }

        // Когато се обхожда директорията със заданията и самата основна директория влиза в списъка,
        // затова трябва да я изключим
        if (path.equals(basePath)) {
            return false;
        }

        if (!Files.exists(path.resolve(MANIFEST_FILE_NAME))) {
            log.warn("Assignment directory " + path + " is missing " + MANIFEST_FILE_NAME + ". Skipping.");

            return false;
        }

        return true;
    }

}
