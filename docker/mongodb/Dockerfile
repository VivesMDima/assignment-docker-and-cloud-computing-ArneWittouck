FROM mongo:latest
COPY dump /dump
CMD mongod --bind_ip_all --fork --logpath /var/log/mongod.log && mongorestore --host localhost:27017 --drop /dump