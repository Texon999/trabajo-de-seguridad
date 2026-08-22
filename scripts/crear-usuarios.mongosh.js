// Crea los usuarios de MongoDB.
// Ejecutar ANTES de activar authorization: enabled en mongod.cfg.
//   mongosh --host localhost --port 27017 scripts/crear-usuarios.mongosh.js
//
// La app NUNCA debe usar el usuario admin ni root.

use admin;

db.createUser({
  user: 'adminMongo',
  pwd: 'CambiaEstaClaveAdmin!',
  roles: [{ role: 'userAdminAnyDatabase', db: 'admin' }]
});

use tienda_db;

// Usuario de la aplicacion con MINIMO privilegio: solo lectura/escritura de tienda_db
db.createUser({
  user: 'tienda_user',
  pwd: 'CambiaEstaClaveApp!',
  roles: [{ role: 'readWrite', db: 'tienda_db' }]
});

print('Usuarios creados correctamente');
