# Dockerfile
FROM mysql:8.0

# Create a new system user named 'sales'
RUN useradd -m sales
# Add a confirmation message for user creation
RUN echo "sales user created successfully."

# Correct way to set a password for the 'sales' system user
RUN echo "sales:your_password" | chpasswd
# Add a confirmation message for password setting
RUN echo "Password set for sales user."


ENV MYSQL_ROOT_PASSWORD=password
ENV MYSQL_DATABASE=sales

# Copy your SQL dump file into the entrypoint initialization directory
COPY test.sql /docker-entrypoint-initdb.d/
