# CS 425: Database Organization Final Project

Java Swing implementation of a parking database system by Tommy Vadakumchery, Ibrahim Marou, and William William.

## Setup

First, clone this repository onto your machine.

Then, open up postgres (on Windows: Start Menu > Search "psql" > Press Enter). Log into your postgres superuser. Run the following commands:

```create user parking_admin with password 'admin' createdb;```

```create database parking with owner parking_admin;```

Copy the path to `SchemaSetup.sql` on your local repository, replace all back slashes (\\) with forward slashes (\/), and wrap the path with **single** quotes. Then, run this command in postgres:

```\i <path_string_you_just_made>```

For example, 

```\i 'C:/Users/Tommy Vadakumchery/Documents/IIT/CS425/CS425Project/SchemaSetup.sql'```

The database should now be set up accordingly. Now, you can compile the Java source code and run the application.