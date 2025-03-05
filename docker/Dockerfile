FROM eclipse-temurin:21-jammy
WORKDIR /app
COPY target/pack/bin/main ./target/pack/bin/
COPY target/pack/lib ./target/pack/lib/

CMD ["./target/pack/bin/main"]