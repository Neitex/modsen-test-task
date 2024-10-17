# Configuration service for BookStore project

This service is responsible for managing the configuration of the BookStore project.

## Running

Docker must be used to run this service. The following command will start the service with config
directory in `./config`:

```bash
docker run -d -p 8888:8888 -v $(pwd)/config:/opt/config --name configuration-service configuration-service
```

## Configuration

Configuration directory must be mounted to `/opt/config` in the container. The configuration files
are Spring Boot `<service-name>.properties` files.