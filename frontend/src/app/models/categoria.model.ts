export interface Categoria {
  idCategoria: number;
  nombre: string;
  descripcion?: string;
  icono: 'comida' | 'bebidas' | 'postres';
  subcategoria?: string;
  activa: boolean;
}
