        /// GameHub Engine \\\
      
      ::::::::       :::    :::        Integrantes: Ignacio Paredes, Facundo Benites, Benjamin Cabrera.
    :+:    :+:      :+:    :+:    
   +:+             +:+    +:+          GameHub Store es una solución backend distribuida para una tienda gamer online. El sistema gestiona el catálogo, inventario, órdenes, pagos,
  :#:             +#++:++#++           despachos y promociones mediante una arquitectura basada en microservicios, evitando depender de un sistema monolítico. 
 +#+   +#+#      +#+    +#+
#+#    #+#      #+#    #+#
########       ###    ###

* **Java 21**
* **Spring Boot 4.0.6**
* **Spring Cloud OpenFeign** (Comunicación entre servicios)
* **Base de Datos:** H2 Database (En memoria/Archivo local)
* **Validaciones:** Spring Boot Validation (Bean Validation)

Nuestro sistema esta compuesto por 10 microservicios funcionales los cuales estan enumarados a continuacion:

|  Microservicio  | Puerto | Descripción Principal                                 |
| `msvc-auth`     | `8000` | Gestión de cuentas de acceso y autenticación.         |
| `msvc-order`    | `8001` | Orquestador de compras, stock y descuentos.           |
| `msvc-user`     | `8002` | Administración de perfiles y direcciones de clientes. |
| `msvc-category` | `8004` | Clasificación de productos del catálogo.              |
| `msvc-inventory`| `8005` | Control de stock disponible, reservado y vendido.     |
| `msvc-payment`  | `8007` | Procesamiento y validación de pagos de órdenes.       |
| `msvc-promotion`| `8009` | Gestión de cupones y cálculo de descuentos.           |
| `msvc-review`   | `8040` | Calificación y comentarios de productos.              |
| `msvc-product`  | `8091` | Catálogo de productos gamer.                          |
| `msvc-shipping` | `8090` | Gestión de despachos de órdenes pagadas.              |
