# Backward Compatible Changes
This document is a basic guide on making backward compatible changes. All our changes based on previous release needs to be backward compatible.

### Cloudant Changes
It's backward compatible to make the following changes:
  * Create a new DB
  * Create a new view in an existing DB
  * Create a new index in an existing DB
  * Update an index in an existing DB
  * Delete a DB
  * Delete an existing view

It's usually not backward compatible to make the following changes:
  * Update an existing view
  * Create a new view/index in a huge existing DB

**Note**:
  1. Update an index is considered backward compatible due to the flex syntax of Lucene. If you removed some fields from being indexed, the change might be breaking. In this case, follow the mitigation of "update an existing view".
  1. Delete a DB or a view is safe because `couchdbcmd.py` will not try to delete the DB if you delete it from the `couchdb.conf`. Once the deployment is done, you need to manually delete the DB or view.
  1. Update an existing view will fail the `createdbs` command. Update won't happen.
  1. For a big DB, creating a new view may take long to build. If the view change is for an existing API, consider building the view days earlier verify it works before the deployment.

**Mitigations**:
  * Update an existing view:
    1. If you are sure that the updated view can work with the existing code, update the view manually before the deployment. This should be **carefully** reviewed and verified.
    1. Otherwise, rename the view and make your changes to the view. This converts an update to "Create a new view" and "Delete an existing view". Both operations are backward compatible.
  * Create a new view/index in a huge existing DB:
    1. Create the view a few days before the deployment begins. Verify the view is built before teh deployment.

### PostgreSQL Changes
We use liquibase to manage the upgrade for PostgreSQL. Liquibase will validate the changeSet in XML files matches what were applied to the database stored in `databasechangelog` table. In each commit, you should:
  * **ALWAYS** create new changeSet in the liquibase file
  * **NEVER** modify an existing changeSet

Within the changeset, the following changes are backward compatible:
  * Create a new table
  * Create a new index to an existing table with `CREATE INDEX CONCURRENTLY` option
    * Remember to add `runInTransaction="false"` in the change set to avoid errors.
  * Drop an existing index if that's not used with `DROP INDEX CONCURRENTLY` option
  * Add a new nullable column without default value to a big table
  * Add a new column to a small table (less than 1000 rows)
  * Add a new function

The following changes should be avoided:
  * Drop a table
  * Drop an index which is currently being used
  * Drop a column from an existing table
  * Delete rows which is currently being used
  * Create a new index to a big table without using `CONCURRENTLY` option
  * Add a new column with default value to a big table
  * Update an existing function

**Mitigations**:
  * If there is a change to an existing changeSet already, revert the change and add a new changeSet to apply the same change.
  * If it's necessary to make a drop to old stuff:
    * If it's necessary to drop a table, finish the deployment and truncate the table manually. Then the table can be dropped in next changeSet.
    * If it's necessary to drop an index, and the index is now in use, finish the deployment and make sure it is no longer used. Then drop the index in next changeSet.
    * If it's necessary to drop a column, just leave the column in the table. It's not harmful. Put a comment in the Java class to let maintainer know this is no longer needed.
    * If it's necessary to delete rows, finish the deployment and make sure they are no longer used. Then delete the rows in next changeSet.
    * When possible, leave the stuff without dropping then. If any delayed drop is necessary, it's recommended to make it a separate change. This is to reduece the risk in the subsequent upgrade deployment.
  * If it's necessary to add a new column with default value to a big table, try to handle the default in java.
  * If it's necessary to update a new function, create a new function instead and update all references in Java to use the new function.
  * If it's absolutely necessary to make a table in a non-backward compatible way and that table is not in use yet. Manual action can be taken to make the schema change. Prepare the steps and apply the change in a separate deployment. Also fix the `databasechangelog` table to make sure next liquibase run won't break.

### Java API Changes
The situation of API backward compatibility is more complicated. Here we list some very basic rules.

It's usually backward compatible to make the following changes to Java classes for REST APIs:
  * Add a resource API
  * Add an optional query parameter to a resource API
  * Add a new resource entity
  * Add a new optional property to existing entity
  * Add a new enum value for an input property

It's not backward compatible to make the following changes:
  * Change the type of an existing property
  * Add a new required property to existing entity

The backward compatibility above is conditional. There are a few restrictions on how the client will use the response of the API:
  * When there is an unknown property, the client should not throw error.
  * In PUT request, the client should pass-back the "unknown property".
  * When there is an unknown enum returned, the client should ignore it or fail gracefully. For example, if the client meets an unknown payment instrument type, it should

**Mitigations**:
  * Don't change the type of an existing property. If for example int needs to be changed to string, probably use another property instead. When that is not possible, consider upgrade the client to handle both types in the field and then upgrade the backend.
  * Don't add a new required property. Instead, use a default value for that property.
  * If the client doesn't meet the conditions above, the some of the changes listed in "backward compatable" category will still be breaking. In that case, the client needs to be fixed to accept new properties before the backend can upgrade. Usually the issue can be caught in staging deployment and testing.

### Dataloader Changes
The dataloader is run after every deployment. However the dataloader doesn't do updates. It only creates new stuff.

**Mitigations**:
When update an existing dataloaded content, use API to make the change manually.
