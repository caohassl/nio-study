<!DOCTYPE HTML>
<html>
  <head>
    <title>Direcoty List</title>
    <style type="text/css">
      body {
        width: 80%;
        padding: 0;
        margin: 0 10%;
      }
      td, th {
	    padding: 2px 0;      
      }
      table {
        width: 100%;
      }
      table tr:nth-child(even) {
         background: #F5FAFF;
      }
      .size, .mtime {
         text-align: center;
      }
    </style>
  </head>
  <body>
    <h1>{{dir}}/</h1>
    <table>
      <thead>
        <tr>
          <th>Name</th>
          <th>Size</th>
          <th>Last Modified</th>
        </tr>
      </thead>
      <tbody>
        {{#files}}
        <tr>
          <td><a href="{{href}}">{{name}}</a></td>
          <td class='size'>{{size}}</td>
          <td class='mtime'>{{mtime}}</td>
        </tr>
        {{/files}}
      </tbody>
    </table>
  </body>
</html>
