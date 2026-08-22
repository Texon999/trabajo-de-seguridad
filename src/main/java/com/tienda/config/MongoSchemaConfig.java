package com.tienda.config;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoSchemaConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoSchemaConfig.class);

    private final MongoTemplate mongoTemplate;

    public MongoSchemaConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void aplicarValidaciones() {
        aplicarSchema("productos", schemaProductos());
        aplicarSchema("usuarios", schemaUsuarios());
    }

    private Document schemaProductos() {
        return new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required", List.of("nombre", "precio"))
                        .append("properties", new Document()
                                .append("nombre", new Document("bsonType", "string")
                                        .append("minLength", 2)
                                        .append("maxLength", 100))
                                .append("precio", new Document("bsonType", "double")
                                        .append("minimum", 0))
                                .append("categoria", new Document("bsonType", "string")
                                        .append("maxLength", 50))
                                .append("stock", new Document("bsonType", "int")
                                        .append("minimum", 0))
                                .append("marca", new Document("bsonType", "string")
                                        .append("maxLength", 50))
                                .append("activo", new Document("bsonType", "bool"))
                                .append("creadoEn", new Document("bsonType", "date"))
                                .append("creadoPor", new Document("bsonType", "string"))
                                .append("ipOrigen", new Document("bsonType", "string"))));
    }

    private Document schemaUsuarios() {
        return new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required", List.of("username", "passwordHash", "nombre", "fechaCreacion"))
                        .append("properties", new Document()
                                .append("username", new Document("bsonType", "string")
                                        .append("minLength", 3)
                                        .append("maxLength", 50))
                                .append("passwordHash", new Document("bsonType", "string")
                                        .append("minLength", 8)
                                        .append("maxLength", 100))
                                .append("nombre", new Document("bsonType", "string")
                                        .append("maxLength", 100))
                                .append("fechaCreacion", new Document("bsonType", "date"))
                                .append("ultimoAcceso", new Document("bsonType", "date"))));
    }

    private void aplicarSchema(String coleccion, Document validator) {
        Document comando = new Document("collMod", coleccion)
                .append("validator", validator)
                .append("validationAction", "error");

        try {
            mongoTemplate.getDb().runCommand(comando);
            log.info("Validacion $jsonSchema aplicada a la coleccion {}", coleccion);
        } catch (Exception e) {
            Document crear = new Document("create", coleccion)
                    .append("validator", validator)
                    .append("validationAction", "error");
            try {
                mongoTemplate.getDb().runCommand(crear);
                log.info("Coleccion {} creada con validacion $jsonSchema", coleccion);
            } catch (Exception e2) {
                log.warn("No se pudo configurar $jsonSchema en {}: {}", coleccion, e2.getMessage());
            }
        }
    }

}
