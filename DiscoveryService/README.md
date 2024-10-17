# Discovery service for BookStore project

This service is responsible for managing the discovery of microservices in the BookStore project.

## Running

Docker must be used to run this service. The following command will start the service:

```bash
docker run -d -p 8761:8761 --name discovery-service discovery-service
```

## Configuration

No configuration is required for this service.