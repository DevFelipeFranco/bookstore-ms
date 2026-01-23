# Bookstore Microservices (bookstore-ms)

Sistema de microservicios para gestión de una librería, implementado con **Spring Boot 4.0** y **Java 21**, siguiendo los principios de **Arquitectura Hexagonal (Ports & Adapters)**.

---

## 📐 Arquitectura del Proyecto

El proyecto sigue la **Arquitectura Hexagonal** (también conocida como **Clean Architecture** o **Ports & Adapters**), que permite separar claramente la lógica de negocio de los detalles de infraestructura.

### Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           INFRASTRUCTURE                                 │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                        APPLICATION                               │    │
│  │  ┌───────────────────────────────────────────────────────────┐  │    │
│  │  │                        DOMAIN                              │  │    │
│  │  │  ┌─────────────┐  ┌──────────────┐  ┌────────────────┐   │  │    │
│  │  │  │   Models    │  │ Value Objects│  │    Services    │   │  │    │
│  │  │  │  (Entities) │  │              │  │ (Domain Logic) │   │  │    │
│  │  │  └─────────────┘  └──────────────┘  └────────────────┘   │  │    │
│  │  │                                                           │  │    │
│  │  │  ┌────────────────────┐       ┌────────────────────────┐ │  │    │
│  │  │  │   Ports IN         │       │     Ports SPI          │ │  │    │
│  │  │  │   (Use Cases)      │       │   (Repository Ports)   │ │  │    │
│  │  │  └────────────────────┘       └────────────────────────┘ │  │    │
│  │  └───────────────────────────────────────────────────────────┘  │    │
│  │                              ▲                                   │    │
│  │   ┌──────────────────────────┼───────────────────────────────┐  │    │
│  │   │         Use Case Implementations (Services)              │  │    │
│  │   └──────────────────────────────────────────────────────────┘  │    │
│  └─────────────────────────────────────────────────────────────────┘    │
│                                 ▲                                        │
│  ┌──────────────────────────────┼───────────────────────────────────┐   │
│  │                    Adapters (Persistence, REST, etc.)            │   │
│  └──────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📁 Estructura del Proyecto

```
bookstore-ms/
├── build.gradle                 # Configuración Gradle del proyecto raíz
├── settings.gradle              # Definición de módulos
├── customer-ms/                 # Microservicio de Clientes
│   ├── domain/                  # Capa de Dominio
│   ├── application/             # Capa de Aplicación
│   └── infrastructure/          # Capa de Infraestructura
│       ├── adapter/             # Adaptadores de salida
│       └── persistence/         # Persistencia de datos
│
└── order-ms/                    # Microservicio de Órdenes
    ├── domain/                  # Capa de Dominio
    └── application/             # Capa de Aplicación
```

---

## 🎯 Microservicios

### 1. Customer Microservice (`customer-ms`)

Microservicio encargado de la gestión de clientes.

#### Capas:

| Capa | Descripción | Dependencias |
|------|-------------|--------------|
| **domain** | Contiene la lógica de negocio pura, sin dependencias de frameworks | Solo JUnit para tests |
| **application** | Implementa los casos de uso e integra con Spring Boot | Depende de `domain` y `infrastructure:adapter` |
| **infrastructure:adapter** | Implementa los puertos de salida (repositorios) | Depende de `domain` |
| **infrastructure:persistence** | Capa de persistencia de datos | - |

#### Componentes del Dominio:

- **Models (Entidades)**
  - `Customer` - Entidad principal con lógica de negocio rica
  - `CreditLimit`, `CustomerStatus`, `CustomerType`, `Money`, `PurchaseHistory`

- **Value Objects**
  - `CustomerId`, `Email`, `Address`, `PersonalInfo`

- **Ports IN (Casos de Uso)**
  - `CreateCustomerUseCase` - Crear un nuevo cliente
  - `GetCustomerUseCase` - Obtener información de cliente
  - `UpdateAddressUseCase` - Actualizar dirección
  - `UpdateCreditLimitUseCase` - Actualizar límite de crédito
  - `PromoteToVipUseCase` - Promover cliente a VIP

- **Ports SPI (Interfaces de Repositorio)**
  - `CustomerRepository` - Contrato para persistencia de clientes

- **Domain Services**
  - `DiscountCalculator` - Cálculo de descuentos
  - `PricingStrategy` - Estrategias de precio
  - `ShippingPolicy` - Políticas de envío

- **Exceptions**
  - `CustomerDeactivationException`, `CustomerReactivationException`
  - `InactiveCustomerException`, `InsufficientCreditException`
  - `InvalidCreditLimitException`, `InvalidCustomerIdException`
  - `VipPromotionException`

---

### 2. Order Microservice (`order-ms`)

Microservicio encargado de la gestión de órdenes de compra (en desarrollo).

#### Componentes del Dominio:

- **Models (Entidades)**
  - `Order`, `OrderItem`, `OrderPricing`, `OrderShipping`
  - `Customer`, `Entry`, `AuditTrail`, `Address`

- **Value Objects**
  - `OrderId`, `CustomerId`, `Money`, `Discount`, `OrderStatus`

- **Domain Services**
  - `VolumeDiscountStrategy` - Estrategia de descuento por volumen

---

## 🏗️ Principios de Arquitectura Hexagonal

### Regla de Dependencias

```
Infrastructure → Application → Domain
      ↓               ↓            ↓
  Adaptadores    Casos de Uso   Lógica Pura
```

- **Domain**: No depende de ninguna otra capa. Contiene lógica de negocio pura.
- **Application**: Depende solo del Domain. Orquesta los casos de uso.
- **Infrastructure**: Depende del Domain y Application. Implementa adaptadores.

### Ports & Adapters

| Tipo | Descripción | Ejemplo |
|------|-------------|---------|
| **Port IN** | Interfaces que exponen casos de uso | `CreateCustomerUseCase` |
| **Port SPI** | Interfaces para dependencias externas | `CustomerRepository` |
| **Adapter IN** | Implementa entrada (Controllers) | REST Controllers |
| **Adapter OUT** | Implementa salida (Repositorios) | `CustomerPersistenceAdapter` |

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| **Java** | 21 | Lenguaje de programación |
| **Spring Boot** | 4.0.0 | Framework de aplicación |
| **Spring MVC** | - | API REST |
| **Spring Actuator** | - | Monitoreo y health checks |
| **Gradle** | 8.x | Build tool |
| **Lombok** | - | Reducción de boilerplate |
| **JUnit 5** | 5.10.0 | Testing |

---

## 📦 Gestión de Dependencias entre Módulos

```groovy
// customer-ms:application/build.gradle
dependencies {
    implementation project(":customer-ms:domain")
    implementation project(':customer-ms:infrastructure:adapter')
}
```

La capa de `application` depende de:
- `domain` para acceder a modelos y puertos
- `infrastructure:adapter` para obtener implementaciones de los puertos

---

## 🚀 Ejecución del Proyecto

### Requisitos Previos
- Java 21 (JDK)
- Gradle 8.x

### Comandos

```bash
# Compilar el proyecto
./gradlew build

# Ejecutar customer-ms
./gradlew :customer-ms:application:bootRun

# Ejecutar tests
./gradlew test
```

---

## 📊 Reglas de Negocio (Customer Domain)

La entidad `Customer` encapsula las siguientes reglas de negocio:

1. **Actualizar Límite de Crédito**: No permitido para clientes inactivos o si tienen deuda pendiente.
2. **Promover a VIP**: Requiere ≥10 compras y total de compras >$5000.
3. **Validar Compra**: Verifica crédito disponible y estado activo.
4. **Registrar Compra**: Registra en historial y consume crédito.
5. **Desactivar Cliente**: No permitido con deuda pendiente.
6. **Reactivar Cliente**: No permitido para clientes bloqueados.
7. **Actualizar Dirección**: Solo para clientes activos.
8. **Actualizar Información Personal**: Validación de datos personales.

---

## 📝 Notas de Desarrollo

- El proyecto utiliza **constructor privado + factory methods** para la creación de entidades.
- Los **Value Objects** son inmutables y validados en su construcción.
- Se separan los **Commands** (objetos de entrada) de los modelos de dominio.
- El módulo `order-ms` está parcialmente comentado y en desarrollo.

---

## 🔗 Referencias

- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
