# IT Asset & Helpdesk Management System

Console-based Java project for managing employees, IT assets, asset assignments, and helpdesk tickets.

## Project Structure

```text
src/
  dao/       JDBC database access classes
  db/        Database connection helper
  main/      Console application entry point
  model/     POJO and enum classes
  service/   Business logic layer
sql/
  schema.sql Database and table creation script
  seed.sql   Sample data script
  queries.sql Useful SQL queries
```

## Run In Memory Mode

This mode does not require MySQL. Data exists only while the program is running.

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java .\src | ForEach-Object { $_.FullName })
java -cp out main.Main
```

## Setup MySQL Database

Run these files in MySQL:

```sql
SOURCE sql/schema.sql;
SOURCE sql/seed.sql;
```

If you are using MySQL Workbench, open `sql/schema.sql`, run it, then open and run `sql/seed.sql`.

## Run In Database Mode

Add MySQL Connector/J jar to the classpath, then pass `--db`.

Example:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java .\src | ForEach-Object { $_.FullName })
java -cp "out;lib\mysql-connector-j.jar" main.Main --db
```

Default database settings:

```text
URL: jdbc:mysql://localhost:3306/it_asset_helpdesk
User: root
Password: empty
```

You can override them:

```powershell
java -Ddb.url="jdbc:mysql://localhost:3306/it_asset_helpdesk" -Ddb.user="root" -Ddb.password="your_password" -cp "out;lib\mysql-connector-j.jar" main.Main --db
```
