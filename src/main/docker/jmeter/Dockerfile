FROM debian:bullseye-slim

LABEL application=jmeter

ENV TZ=Etc/UTC \
    APP_USER=appuser

RUN groupadd $APP_USER \
    && useradd -m -g $APP_USER $APP_USER

RUN mkdir -p /usr/share/man/man1
RUN apt-get update -y && apt-get install -y -f wget default-jdk

RUN wget https://mirror.olnevhost.net/pub/apache//jmeter/binaries/apache-jmeter-5.3.tgz
RUN tar xzf apache-jmeter-5.3.tgz
RUN chown -R $APP_USER:$APP_USER apache-jmeter-5.3

ENV PATH "/apache-jmeter-5.3/bin:$PATH"
RUN echo "export PATH=/apache-jmeter-5.3/bin:${PATH}" >> /root/.bashrc

USER $APP_USER
WORKDIR /home/appuser
