package com.tienda.repository;

import com.tienda.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductoRepository extends MongoRepository<Producto, String> {

    List<Producto> findByActivo(Boolean activo);

    List<Producto> findByCategoria(String categoria);

    List<Producto> findByPrecioLessThan(Double precio);

    List<Producto> findByPrecioGreaterThan(Double precio);

    List<Producto> findByMarca(String marca);

    List<Producto> findByCategoriaAndActivo(String categoria, Boolean activo);

    List<Producto> findByPrecioBetween(Double min, Double max);

}
