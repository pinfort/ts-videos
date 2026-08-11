package me.pinfort.tsvideos.manager.api.config

import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Configuration

// Kept off ApiApplication itself: @MapperScan on the primary @SpringBootConfiguration class
// still registers mapper beans inside @WebMvcTest slices (it runs via a
// BeanDefinitionRegistryPostProcessor, which slice-test type filters don't exclude), and those
// mapper beans then fail because the slice never wires up a real SqlSessionFactory. A plain
// @Configuration class is excluded by @WebMvcTest's default filtering, so it only takes effect
// when the full application context loads.
@Configuration
@MapperScan("me.pinfort.tsvideos.core.external.database.mapper")
class MyBatisConfig
