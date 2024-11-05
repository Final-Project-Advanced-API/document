
# Document API

The Document API provides functionality for managing documents within workspaces. It includes endpoints for creating, retrieving, updating, deleting, and managing document statuses (private/public and trash). Each endpoint requires proper authorization and access permissions.

## Table of Contents

- [Create Document]
- [Get All Documents]
- [Get Document by ID]
- [Delete Document by ID]
- [Update Document]
- [Update Document Privacy Status]
- [Update Document Trash Status]
- [Get All Documents by Workspace ID]
- [Delete Documents by Workspace ID]
- [Get All Trash Documents]

## Endpoints

### Create Document

- **URL**: https://document-service.jelay.site/swagger-ui/index.html#/document-controller/createDocument
- **Method**: `POST`
- **Description**: Creates a new document within a workspace. Only an admin of the workspace can create a document.
- **Request Body**:

    ```json
    {
      "workspaceId": "45fa3ebd-7888-49b3-bb19-c9ad70cdb4ac",
      "title": "Api Gateway",
      "contents": [
      {
        "id": "6f0dea5a-8e7c-49c0-8c9a-ed4fc03833a6",
        "type": "heading",
        "props": {
          "textColor": "default",
          "backgroundColor": "default",
          "textAlignment": "center",
          "level": 1
        },
        "content": [
          {
            "type": "text",
            "text": "H",
            "styles": {
              "textColor": "red"
            }
          }
        ],
        "children": []
      }
      ]
    }
    ```

- **Response**:

    ```json
    {
      "message": "Create document successfully",
      "payload": {
        "documentId": "a3a6ecb3-a9bd-4042-af5b-0d5cd0d917d0",
        "workspaceId": "45fa3ebd-7888-49b3-bb19-c9ad70cdb4ac",
        "title": "Api Gateway",
        "contents": [
          {
            "id": "6f0dea5a-8e7c-49c0-8c9a-ed4fc03833a6",
            "type": "heading",
            "props": {
              "textColor": "default",
              "backgroundColor": "default",
              "textAlignment": "center",
              "level": 1
          },
          "content": [
            {
              "type": "text",
              "text": "H",
              "styles": {
                "textColor": "red"
              }
            }
          ],
          "children": []
        }
      ],
        "isPrivate": true,
        "isDeleted": false,
        "createdAt": "2024-11-02T06:59:57.533978862",
        "updatedAt": "2024-11-02T06:59:57.533991417"
      },
      "status": "CREATED",
      "statusCode": 201,
      "timestamp": "2024-11-02T06:59:58.023237902"
    }
    
    ```


### Get All Documents

- **URL**: https://document-service.jelay.site/swagger-ui/index.html#/document-controller/getAllDocument
- **Method**: `GET`
- **Description**: Retrieves a paginated list of all documents that are not marked as trash. Only users associated with the workspace can view its documents.
- **Query Parameters**:
  - `pageNo` (default: `1`): Page number (must be greater than 0).
  - `pageSize` (default: `5`): Number of documents per page (must be greater than 0).
  - `sortBy`: Field to sort by (`CREATED_AT`, `UPDATED_AT`, `TITLE`, `PRIVATE`).
  - `sortDirection`: Sort direction (`ASC` or `DESC`).
- **Response**:

    ```json
    {
      "message": "Create document successfully",
      "payload": [ 
        {
        "documentId": "a3a6ecb3-a9bd-4042-af5b-0d5cd0d917d0",
        "workspaceId": "45fa3ebd-7888-49b3-bb19-c9ad70cdb4ac",
        "title": "Api Gateway",
        "contents": [
          {
            "id": "6f0dea5a-8e7c-49c0-8c9a-ed4fc03833a6",
            "type": "heading",
            "props": {
              "textColor": "default",
              "backgroundColor": "default",
              "textAlignment": "center",
              "level": 1
          },
          "content": [
            {
              "type": "text",
              "text": "H",
              "styles": {
                "textColor": "red"
              }
            }
          ],
          "children": []
        }
      ],
        "isPrivate": true,
        "isDeleted": false,
        "createdAt": "2024-11-02T06:59:57.533978862",
        "updatedAt": "2024-11-02T06:59:57.533991417"
      },
      {
        "documentId": "416f883f-9722-492d-848a-de665ddcc14f",
        "workspaceId": "45fa3ebd-7888-49b3-bb19-c9ad70cdb4ac",
        "title": "Eureka Server",
        "contents": [
          {
            "id": "6f0dea5a-8e7c-49c0-8c9a-ed4fc03833a6",
            "type": "heading",
            "props": {
              "textColor": "default",
              "backgroundColor": "default",
              "textAlignment": "center",
              "level": 1
            },
            "content": [
              {
                "type": "text",
                "text": "H",
                "styles": {
                  "textColor": "red"
                }
              }
            ],
            "children": []
          }
        ],
        "isPrivate": true,
        "isDeleted": false,
        "createdAt": "2024-11-02T07:02:19.902",
        "updatedAt": "2024-11-02T07:05:50.286862375"
      }
      ]
    }
    
    ```


### Get Document by ID

- **URL**: https://document-service.jelay.site/swagger-ui/index.html#/document-controller/getDocument
- **Method**: `GET`
- **Description**: Retrieves the details of a document by its ID.
    - If the document is private, only users associated with the workspace that contains this document can access it.
    - If the document is public, any user (even those not associated with the workspace) can access it.
- **Path Parameters**:
  - `documentId`: The UUID of the document.
- **Response**:

    ```json
    {
      "message": "Get document successfully",
      "payload": {
        "documentId": "416f883f-9722-492d-848a-de665ddcc14f",
        "workspaceId": "45fa3ebd-7888-49b3-bb19-c9ad70cdb4ac",
        "title": "Config Server",
        "contents": [
          {
            "id": "6f0dea5a-8e7c-49c0-8c9a-ed4fc03833a6",
            "type": "heading",
            "props": {
              "textColor": "default",
              "backgroundColor": "default",
              "textAlignment": "center",
              "level": 1
            },
            "content": [
              {
                "type": "text",
                "text": "H",
                "styles": {
                  "textColor": "red"
                }
              }
            ],
            "children": []
          }
        ],
        "isPrivate": true,
        "isDeleted": false,
        "createdAt": "2024-11-02T07:02:19.902",
        "updatedAt": "2024-11-02T07:02:19.902"
      },
      "status": "OK",
      "statusCode": 200,
      "timestamp": "2024-11-02T07:03:37.971729966"
    }
    ```


### Delete Document by ID

- **URL**: `https://document-service.jelay.site/swagger-ui/index.html#/document-controller/deleteDocument`
- **Method**: `DELETE`
- **Description**: Deletes a document by its ID. Only an admin of the workspace that contains this document can delete it.
- **Path Parameters**:
  - `documentId`: The list of UUID of the document.
- **Response**:

    ```json
    {
      "message": "Document deleted successfully",
      "status": "OK",
      "statusCode": 200,
      "timestamp": "2024-11-01T14:19:38.9570009"
    }
    
    ```


### Update Document

- **URL**: `https://document-service.jelay.site/swagger-ui/index.html#/document-controller/updateDocument`
- **Method**: `PUT`
- **Description**: Updates an existing document by its ID. Only an admin of the workspace that contains this document can update it.
- **Path Parameters**:
  - `documentId`: The UUID of the document.
- **Request Body**:

    ```json
    {
      "title": "Eureka Server",
      "contents": [
          {
            "id": "6f0dea5a-8e7c-49c0-8c9a-ed4fc03833a6",
            "type": "heading",
            "props": {
              "textColor": "default",
              "backgroundColor": "default",
              "textAlignment": "center",
              "level": 1
            },
            "content": [
              {
                "type": "text",
                "text": "H",
                "styles": {
                  "textColor": "red"
                }
              }
            ],
            "children": []
          }
        ]
    }
    
    ```

- **Response**:

    ```json
    {
      "message": "Update document successfully",
      "payload": {
        "documentId": "416f883f-9722-492d-848a-de665ddcc14f",
        "workspaceId": "45fa3ebd-7888-49b3-bb19-c9ad70cdb4ac",
        "title": "Eureka Server",
        "contents": [
          {
            "id": "6f0dea5a-8e7c-49c0-8c9a-ed4fc03833a6",
            "type": "heading",
            "props": {
              "textColor": "default",
              "backgroundColor": "default",
              "textAlignment": "center",
              "level": 1
            },
            "content": [
              {
                "type": "text",
                "text": "H",
                "styles": {
                  "textColor": "red"
                }
              }
            ],
            "children": []
          }
        ],
        "isPrivate": true,
        "isDeleted": false,
        "createdAt": "2024-11-02T07:02:19.902",
        "updatedAt": "2024-11-02T07:05:50.286862375"
      },
      "status": "OK",
      "statusCode": 200,
      "timestamp": "2024-11-02T07:05:50.416503579"
    }
    
    ```


### Update Document Privacy Status

- **URL**: https://document-service.jelay.site/swagger-ui/index.html#/document-controller/updateStatusDocument
- **Method**: `PUT`
- **Description**: Updates the privacy status of a document (public or private). Only an admin of the workspace that contains this document can update it.
- **Path Parameters**:
  - `documentId`: The UUID of the document.
- **Response**:

    ```json
    {
      "message": "Update status document successfully",
      "status": "OK",
      "statusCode": 200,
      "timestamp": "2024-11-02T07:07:24.361759522"
    }
    
    ```


### Update Document Trash Status

- **URL**: https://document-service.jelay.site/swagger-ui/index.html#/document-controller/updateStatusDelete
- **Method**: `PUT`
- **Description**: Updates the trash status of a document. Only an admin of the workspace that contains this document can update it.
- **Path Parameters**:
  - `documentId`: The list of UUID of the document.
- **Response**:

    ```json
    {
      "message": "Update status document successfully",
      "status": "OK",
      "statusCode": 200,
      "timestamp": "2024-11-02T07:06:41.389327878"
    }
    ```


### Get All Documents by Workspace ID

- **URL**: https://document-service.jelay.site/swagger-ui/index.html#/document-controller/getAllDocumentByWorkspaceId
- **Method**: `GET`
- **Description**: Retrieves all documents associated with a specific workspace. Only users associated with the workspace can retrieve these documents.
- **Path Parameters**:
  - `workspaceId`: The UUID of the workspace.
- **Response**:

    ```json
    {
      "message": "Get all document successfully",
      "payload": [
        {
          "documentId": "a3a6ecb3-a9bd-4042-af5b-0d5cd0d917d0",
          "workspaceId": "45fa3ebd-7888-49b3-bb19-c9ad70cdb4ac",
          "title": "Api Gateway",
          "contents": [
            {
              "id": "6f0dea5a-8e7c-49c0-8c9a-ed4fc03833a6",
              "type": "heading",
              "props": {
                "textColor": "default",
                "backgroundColor": "default",
                "textAlignment": "center",
                "level": 1
              },
              "content": [
                {
                  "type": "text",
                  "text": "H",
                  "styles": {
                    "textColor": "red"
                  }
                }
              ],
              "children": []
            }
          ],
          "isPrivate": true,
          "isDeleted": false,
          "createdAt": "2024-11-02T06:59:57.533",
          "updatedAt": "2024-11-02T06:59:57.533"
        }
      ],
      "status": "OK",
      "statusCode": 200,
      "timestamp": "2024-11-02T07:08:51.225967343"
    }
    
    ```


### Delete Documents by Workspace ID

- **URL**: https://document-service.jelay.site/swagger-ui/index.html#/document-controller/deleteDocumentByWorkspaceId
- **Method**: `DELETE`
- **Description**: Deletes all documents associated with a specific workspace. Only an admin of the workspace can delete documents within it.
- **Path Parameters**:
  - `workspaceId`: The UUID of the workspace.
- **Response**:

    ```json
    {
      "message": "Delete document by workspace id successfully",
      "status": "OK",
      "statusCode": 200,
      "timestamp": "2024-11-02T07:10:40.798749453"
    }
    ```


### Get All Trash Documents

- **URL**: https://document-service.jelay.site/swagger-ui/index.html#/document-controller/getAllTrashDocument
- **Method**: `GET`
- **Description**: Retrieves all documents marked as trash. Only an admin can view these trash documents.
- **Query Parameters**:
  - `pageNo` (default: `1`): Page number (must be greater than 0).
  - `pageSize` (default: `5`): Number of documents per page (must be greater than 0).
  - `sortBy`: Field to sort by (`CREATED_AT`, `UPDATED_AT`, `TITLE`, `PRIVATE`).
  - `sortDirection`: Sort direction (`ASC` or `DESC`).
- **Response**:

    ```json
    {
      "message": "Get all trash document successfully",
      "payload": {
        "documentId": "05f65368-9e25-4e14-9b45-45ea96e835a0",
        "workspaceId": "45fa3ebd-7888-49b3-bb19-c9ad70cdb4ac",
        "title": "Config Server",
        "contents": [
          {
            "id": "6f0dea5a-8e7c-49c0-8c9a-ed4fc03833a6",
            "type": "heading",
            "props": {
              "textColor": "default",
              "backgroundColor": "default",
              "textAlignment": "center",
              "level": 1
            },
            "content": [
              {
                "type": "text",
                "text": "H",
                "styles": {
                  "textColor": "red"
                }
              }
            ],
            "children": []
          }
        ],
        "isPrivate": true,
        "isDeleted": true,
        "createdAt": "2024-11-02T07:11:44.078485173",
        "updatedAt": "2024-11-02T07:11:44.078490083"
      },
      "status": "CREATED",
      "statusCode": 201,
      "timestamp": "2024-11-02T07:11:44.117038288"
    }
    ```