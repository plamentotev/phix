# Приложно-програмен интерфейс

Phix предоставя уеб базиран приложно-програмен интерфейс за предаване на задачи и автоматичната им проверка.
В таблицата по-долу са описани предоставените REST ресурси:

<table>
    <thead>
        <tr>
            <th>Ресурс</th>
            <th>HTTP глагол</th>
            <th>Описание</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>/assignments</code></td>
            <td>GET</td>
            <td>
                <p>Връща списък със заданията.</p>
                <p><strong>Примерен отговор:</strong></p>
                <p>HTTP код 200:</p>
                <p>
                    <code><pre>
[{
    "id":"multiplication",
    "title":"Умножение",
    "type":"docker"
},
{
    "id":"hello-world",
    "title":"Hello World",
    "type":"docker"
}]
                    </pre></code>
                </p>
            </td>
        </tr>
        <tr>
            <td><code>/assignments/:id</code></td>
            <td>GET</td>
            <td>
                <p>Връща детайлно описание на дадено задание.</p>
                <p><strong>Параметри:</strong></p>
                <p><code>id</code> - идентификатора на заданието</p>
                <p><strong>Примерен отговор:</strong></p>
                <p>HTTP код 200:</p>
                <p>
                    <code><pre>
{
    "id":"multiplication",
    "type":"docker",
    "title":"Умножение",
    "description":"Напишете програма на Java, която за вход получава цяло числото n, последвано от n на брой редове, съдържащи двойка цели числа. За изход програмата да връща резултата от умножението на двойките числа",
    "initialCode":"public class Multiplication {\n\n public static void main(String[] args) {\n // Напишете решението си тук\n }\n\n}\n"
}
                    </pre></code>
                </p>
                <p>HTTP код 404: ако няма задание с дадения идентификатор.</p>
                <p>
                    <code><pre>
{
    "status": "404",
    "error": "Not Found",
    "message": "Assignment not found"
}
                    </pre></code>
                </p>
            </td>
        </tr>
        <tr>
            <td><code>/assignments/:id/submissions</code></td>
            <td>POST</td>
            <td>
                <p>Предаване на решение за дадено задание. Решението се подава в тялото на заявката.</p>
                <p><strong>Параметри:</strong></p>
                <p><code>id</code> - идентификатора на заданието</p>
                <p><strong>Примерен отговор:</strong></p>
                <p>HTTP код 201:</p>
                <p>
                    <code><pre>
{
    "id": "aa609185-0953-421d-887f-eef3eaf01f91",
    "assignmentId": "multiplication",
    "timestamp": 1506242257318,
    "submissionStatus": "SUBMITTED",
    "message": ""
}
                    </pre></code>
                </p>
                <p>HTTP код 400: ако решението надвишава максимално позволения размер.</p>
                <p>
                    <code><pre>
{
    "status": "400",
    "error": "Bad Request",
    "message": "Invalid payload."
}
                    </pre></code>
                </p>
                <p>HTTP код 404: ако няма задание с дадения идентификатор.</p>
                <p>
                    <code><pre>
{
    "status": "404",
    "error": "Not Found",
    "message": "Assignment not found"
}
                    </pre></code>
                </p>
            </td>
        </tr>
        <tr>
            <td><code>/submissions/:id</code></td>
            <td>GET</td>
            <td>
                <p>Връща резултата от изпълнението на предадено решение.</p>
                <p><strong>Параметри:</strong></p>
                <p><code>id</code> - идентификатора на предаденото решение</p>
                <p><strong>Примерен отговор:</strong></p>
                <p>HTTP код 200:</p>
                <p>
                    <code><pre>
{
    "id":"cb0cff31-9842-410a-b46b-33b20f59ad2e",
    "assignmentId":"multiplication",
    "timestamp":1506266915705,
    "submissionStatus":"COMPILATION_ERROR",
    "message":"Multiplication.java:6: error: ';' expected\nint value = a * b\n^\n1 error\n"
}
                    </pre></code>
                </p>
                <p>HTTP код 404: ако няма предадено решение с дадения идентификатор.</p>
                <p>
                    <code><pre>
{
    "status":"404",
    "error":"Not Found",
    "message":"Submission not found"
}
                    </pre></code>
                </p>
            </td>
        </tr>
        <tr>
            <td><code>/submissions/:id/file</code></td>
            <td>GET</td>
            <td>
                <p>Връща предадено решение.</p>
                <p><strong>Параметри:</strong></p>
                <p><code>id</code> - идентификатора на предаденото решение</p>
                <p><strong>Примерен отговор:</strong></p>
                <p>HTTP код 200:</p>
                <p>Предаденото решение е част от тялото на отговора на заявката</p>
                <p>HTTP код 404: ако няма предадено решение с дадения идентификатор.</p>
                <p>
                    <code><pre>
{
    "status":"404",
    "error":"Not Found",
    "message":"Submission not found"
}
                    </pre></code>
                </p>
            </td>
        </tr>
    </tbody>
</table>