# Guía de Uso de Redis (Caché y Almacenamiento Temporal) en Restobar

## 🛠️ ¿Cómo usar Redis en el Backend (Spring Boot)?

### 1. Inyección de la Dependencia Genérica

Cuando necesiten guardar o leer algo de memoria rápida temporal (Caché de productos, el Carrito de Compras de `CU20`, sesiones expirables, etc.), todo lo que deben hacer es inyectar el Bean `RedisTemplate<String, Object>`.

### 2. Ejemplo Práctico: Guardar un Carrito de Compras (CU20)

```java
package org.restobar.gaira.modulo_ecommerce.carrito.servicio;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CarritoOnlineService {

    // 1. Inyectar la plantilla genérica, que ya está configurada por Spring
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Guarda el carrito asociado a un ID de sesión.
     */
    public void guardarCarrito(String idSesion, CarritoDTO carrito) {
        String key = "carrito:" + idSesion;

        // 2. Guardar asumiendo que "carrito" se convertirá a JSON solo.
        redisTemplate.opsForValue().set(key, carrito);

        // 3. Fijar el TTL (Tiempo de Vida). Según el documento CU20, deben ser 30 mins
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    /**
     * Recupera el carrito o retorna Null si ya expiró la sesión.
     */
    public CarritoDTO obtenerCarrito(String idSesion) {
        String key = "carrito:" + idSesion;
        
        // 4. Se recupera de Redis y se castea a la clase esperada
        return (CarritoDTO) redisTemplate.opsForValue().get(key);
    }
    
    /**
     * Eliminar el carrito manualmente tras concretar el pago (Checkout)
     */
    public void limpiarCarrito(String idSesion) {
        redisTemplate.delete("carrito:" + idSesion);
    }
}
```

### 3. Ejemplo Práctico: Usar anotaciones para Cacheo de Catálogos (CU19)

Si solo necesitan guardar temporalmente en memoria una lista de productos para no sobrecargar PostgreSQL (que según CU19 debe durar 15 minutos), pueden usar Spring Cache nativo. (*Recuerden habilitar `@EnableCaching` en el main de la app de ser necesario*).

```java
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CatalogoService {

    // Guarda el resultado de este método en Redis bajo la llave "catalogo_sucursal_X"
    @Cacheable(value = "catalogos", key = "#idSucursal")
    public List<ProductoCatalogoDTO> obtenerCatalogoPorSucursal(Long idSucursal) {
        // ... Lógica pesada que va a la base de datos ...
        // La segunda vez que llamen esto, ni siquiera entrará a este método, 
        // lo sacará ultrarrápido de Redis automáticamente.
        return repositorio.buscarCatalogo(idSucursal);
    }
}
```

---

## 🚦 Reglas y Buenas Prácticas
1. **Nomenclatura (Keys):** Dado que Redis guarda las llaves como texto plano global, agrupen sus entidades con ":" (ej: `carrito:123`, `sesion:user:45`, `catalogo:sucursal:10`).
2. **TTL Siempre:** Acostumbren a añadir `.expire(...)` a todos sus inserts operativos para asegurarse de que el servidor Redis no se desborde de memoria infinita con carritos de compras fantasma de hace 6 meses.
3. No necesitan reiniciar nada, ni crear repositorios nuevos, ¡solo inyectar `RedisTemplate`!