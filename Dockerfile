FROM jenkins/jenkins:lts

USER root

# Install Maven, Git, curl, unzip
RUN apt-get update && \
    apt-get install -y maven git curl unzip && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Install kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

USER jenkins


