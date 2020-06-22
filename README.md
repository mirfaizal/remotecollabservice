# REMOTE-COLLAB

## To run the service locally you'll need to install postgres
Step 1: install homebrew https://brew.sh/

Step 2: install postgres `brew install postgres`

Step 3: start postgres `brew services start postgresql`

## If you've already installed postgres and the service is running

Step 1: log into postgres `psql postgres`

Step 2: once in postgres, create a user 'remotecollabapp' `CREATE ROLE remotecollabapp WITH LOGIN` (don't forget the semicolon or it won't execute)

Step 3: alter the new role to be a superuser  `ALTER ROLE remotecollabapp SUPERUSER;`

Step 4:
log into postgres as the new mlpdocapp user `psql postgres -U remotecollabapp`

Step 5:
run `CREATE DATABASE remotecollab;`

Step 6:
run `GRANT ALL ON DATABASE remotecollab to remotecollabapp;`

Step 7:
quit out of postgres by running `\quit`

Step 8:
navigate in your unix shell to where you have scripts created or use the one provided in resources/db dir
run `psql -U remotecollabapp -f V1_0__CREATE_T.sql remotecollab;`
run `psql -U remotecollabapp -f afterMigrate_grants.sql remotecollab;`

Step 11:
log back into postgres as remotecollabapp, connect to the remotecollab and insert a record into your new table with the following 
```
psql postgres -U remotecollabapp
\c remotecollab
INSERT INTO document_metadata (bucketid, filename, fileextension, username, createddatetime, additionalinfo) VALUES('buckettest', 'failedDoc1', 'pdf', 'saamiya.shaikh', '2017-03-14', '{"educationType":"Grade 1 Math","status":"s3-bucket-uploaded","firstName":"MyFirstName","lastName":"MyLastName","userRole":"admin"}');
```
the above line can be edited and rerun to upload multiple records for testing. Or you can create a file with multiple inserts defined, and run the following
`psql -f datafilename.sql remotecollab`