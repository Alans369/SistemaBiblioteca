CREATE TABLE Usuarios (
        UsuarioID INT PRIMARY KEY IDENTITY(1,1), -- ID único para cada usuario, auto-incremental
        NombreUsuario NVARCHAR(50) NOT NULL UNIQUE, -- Nombre de usuario para iniciar sesión, debe ser único
        Contrasena NVARCHAR(255) NOT NULL, -- Contraseña del usuario (considera almacenar hashes, no texto plano)
        CorreoElectronico NVARCHAR(100) UNIQUE, -- Correo electrónico del usuario, único
        FechaRegistro DATETIME DEFAULT GETDATE() -- Fecha y hora de registro del usuario
);
GO

-- ### Tabla Categorias
-- Define las diferentes categorías o géneros de libros
CREATE TABLE Categorias (
        CategoriaID INT PRIMARY KEY IDENTITY(1,1), -- ID único para cada categoría, auto-incremental
        NombreCategoria NVARCHAR(50) NOT NULL UNIQUE, -- Nombre de la categoría (ej: 'Ciencia Ficción', 'Historia')
        Descripcion NVARCHAR(255) -- Breve descripción de la categoría
);
GO

-- ### Tabla Libros
-- Almacena la información principal de cada libro con los campos simplificados
CREATE TABLE Libros (
        LibroID INT PRIMARY KEY IDENTITY(1,1), -- ID único para cada libro, auto-incremental
        Titulo NVARCHAR(200) NOT NULL, -- Título del libro
        Autor NVARCHAR(150) NOT NULL, -- Autor del libro
        ImagenURL NVARCHAR(500), -- URL o ruta a la imagen de la portada del libro
        Descripcion NVARCHAR(MAX), -- Descripción o resumen del libro (NVARCHAR(MAX) para texto largo)
        RutaArchivoPDF NVARCHAR(500) NOT NULL, -- Ruta o nombre del archivo PDF en el servidor/almacenamiento
        CategoriaID INT, -- Clave foránea que enlaza con la tabla Categorias
        FechaSubida DATETIME DEFAULT GETDATE(), -- Fecha y hora en que se subió el libro a la biblioteca
        FOREIGN KEY (CategoriaID) REFERENCES Categorias(CategoriaID) -- Define la relación con la tabla Categorias
);
GO