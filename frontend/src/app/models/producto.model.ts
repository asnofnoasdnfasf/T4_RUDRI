import { Categoria } from './categoria.model';

export interface Producto {
  idProducto: number;
  idCategoria: number;
  categoria?: Categoria;
  nombre: string;
  subcategoria?: string;
  descripcion?: string;
  precio: number;
  disponible: boolean;
  imagenUrl: string;
}
