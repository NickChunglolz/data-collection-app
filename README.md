# Assessment

## Overview

APIs for operations between data collections and data files.

## Requirements

1. Data File
    * Contains input information from users
    * Classified by type of data (3 types: 1, 2, or 3)
    * Must pass validation before being used in the application
2. Data Collection
    * A set of multiple Data Files
    * Must contain exactly one Data File from each type
    * Must not contain any Data File that has not passed validation
    * Must never be hard deleted (retain a record as an audit trail)

## Tasks

1. Implement RESTful APIs for Data Collections
    * Standard CRUD operations:
        * Create a new Data Collection
        * Read a Data Collection by ID
        * Update an existing Data Collection
        * Delete a Data Collection (soft delete)
2. Implement a RESTful API to list Data Collections
    * Support filtering and sorting by any attribute
    * Examples:
        * Ordered list of Data Collections by date created
        * Search for all Data Collections containing a substring in the note attribute

## API Endpoints

1. Create Data Collection
    * POST /data-collections
    * Request body: Data Collection object with 3 Data Files (one from each type)
2. Read Data Collection
    * GET /data-collections/{id}
    * Response: Data Collection object with 3 Data Files
3. Update Data Collection
    * PUT /data-collections/{id}
    * Request body: Updated Data Collection object with 3 Data Files
4. Delete Data Collection (soft delete)
    * DELETE /data-collections/{id}
    * Response: Success message (Data Collection marked as deleted)
5. Query Data Collections
    * Post /data-collections/query
    * Request body: filter and sort by any attribute (e.g., date created, note)
    * Response: List of Data Collection objects with filtering and sorting applied

## How To Run?

- First Step - Go to root project folder

    ```
    cd ./path/of/the/project
    ```

- Second Step - Start the database ( docker-compose )

  docker-compose.yml
    ```
    cd ./setup
    docker-compose up --build
    ```

- Third Step - Run Java

  Back to root folder
    ```
    ./gradlew bootRun
    ```