package com.tienda;

import com.tienda.repository.ProductoRepository;
import com.tienda.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
class TiendaApplicationTests {

	@MockBean
	private UsuarioRepository usuarioRepository;

	@MockBean
	private ProductoRepository productoRepository;

	@MockBean
	private MongoTemplate mongoTemplate;

	@Test
	void contextLoads() {
	}

}
