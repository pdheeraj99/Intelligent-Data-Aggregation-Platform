# IDAP Config Repository

This folder contains configuration files that need to be pushed to the separate config repository:
**<https://github.com/pdheeraj99/Intelligent-Data-Aggregation-Platform-Config-Server>**

## Files

| File | Description |
|------|-------------|
| `application.yml` | Shared configuration for all services |
| `eureka-server.yml` | Eureka Discovery Server configuration |
| `api-gateway.yml` | API Gateway routes and settings |
| `user-service.yml` | User Service database and JWT settings |
| `weather-service.yml` | Weather Service API and cache settings |

## How to Push

Run these commands to push the config files to the config repository:

```bash
# Clone the config repository
git clone https://github.com/pdheeraj99/Intelligent-Data-Aggregation-Platform-Config-Server.git temp-config-repo

# Copy all yml files to the config repo
copy *.yml temp-config-repo/

# Navigate to config repo
cd temp-config-repo

# Add, commit and push
git add .
git commit -m "Add Phase 1 service configurations"
git push origin main

# Go back and clean up
cd ..
rmdir /s /q temp-config-repo
```

Or you can manually copy these files to the config repository.
