import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { API_CONFIG } from './api.config';
import { Producto } from '../models/producto.model';

const STORAGE_PRODUCTOS = 'rudri_productos_data';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private readonly http = inject(HttpClient);

  readonly productos = signal<Producto[]>(this.cargarProductosLocales());

  private validarYNormalizar<T extends Omit<Producto, 'idProducto'>>(prod: T): { error?: string; normalizado?: T } {
    const nombreLimpio = (prod?.nombre || '').trim().toUpperCase();
    if (!nombreLimpio) {
      return { error: 'El nombre del producto es obligatorio.' };
    }
    const precioNum = Number(prod?.precio);
    if (!Number.isFinite(precioNum) || precioNum <= 0) {
      return { error: 'El precio del producto debe ser mayor a cero.' };
    }
    return {
      normalizado: {
        ...prod,
        nombre: nombreLimpio,
        precio: Number(precioNum.toFixed(2))
      }
    };
  }

  private obtenerProductosIniciales(): Producto[] {
    return [
      {
        idProducto: 1,
        idCategoria: 1,
        nombre: 'CEVICHE CLÁSICO DRAGÓN MARINO',
        subcategoria: 'Mariscos',
        descripcion: 'Pesca fresca del día al limón sutil con choclo tierno y camote glaseado.',
        precio: 28.0,
        disponible: true,
        imagenUrl: 'https://images.unsplash.com/photo-1535399831218-d5bd36d1a6b3?auto=format&fit=crop&w=300&q=80'
      },
      {
        idProducto: 2,
        idCategoria: 1,
        nombre: 'ARROZ CHAUFA DE MARISCOS',
        subcategoria: 'Mariscos',
        descripcion: 'Arroz al wok salteado con langostinos, calamar, pulpo y salsa oriental.',
        precio: 18.0,
        disponible: true,
        imagenUrl: 'https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=300&q=80'
      },
      {
        idProducto: 3,
        idCategoria: 1,
        nombre: 'SOPA CRIOLLA ESPECIAL',
        subcategoria: 'Sopas',
        descripcion: 'Concentrado tradicional con cabello de ángel, leche y huevo escalfado.',
        precio: 22.0,
        disponible: false,
        imagenUrl: 'https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=300&q=80'
      },
      {
        idProducto: 4,
        idCategoria: 1,
        nombre: 'JALEA MIXTA REAL',
        subcategoria: 'Mariscos',
        descripcion: 'Chicharrón crocante de pescado y mariscos con yucas doradas y zarza criolla.',
        precio: 42.0,
        disponible: true,
        imagenUrl: 'https://images.unsplash.com/photo-1562967914-608f82629710?auto=format&fit=crop&w=300&q=80'
      },
      {
        idProducto: 5,
        idCategoria: 1,
        nombre: 'TACU TACU EN SALSA DE MARISCOS',
        subcategoria: 'Arroces',
        descripcion: 'Tacu tacu dorado bañado en cremosa salsa de mariscos seleccionados.',
        precio: 32.0,
        disponible: true,
        imagenUrl: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=300&q=80'
      },
      {
        idProducto: 6,
        idCategoria: 2,
        nombre: 'CHICHA MORADA (JARRA 1L)',
        subcategoria: 'Jarras',
        descripcion: 'Chicha natural de maíz morado hervida con piña, membrillo, manzana y canela.',
        precio: 12.0,
        disponible: true,
        imagenUrl: 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=300&q=80'
      },
      {
        idProducto: 7,
        idCategoria: 2,
        nombre: 'LIMONADA FROZEN (JARRA 1L)',
        subcategoria: 'Jarras',
        descripcion: 'Limonada frappé refrescante recién preparada.',
        precio: 14.0,
        disponible: true,
        imagenUrl: 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=300&q=80'
      },
      {
        idProducto: 8,
        idCategoria: 3,
        nombre: 'PICARONES CON MIEL DE CHANCACA',
        subcategoria: 'Tradicionales',
        descripcion: 'Aros crocantes de zapallo y camote bañados en miel artesanal de hoja de higo.',
        precio: 15.0,
        disponible: true,
        imagenUrl: 'https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=300&q=80'
      },
      {
        idProducto: 9,
        idCategoria: 3,
        nombre: 'SUSPIRO A LA LIMEÑA',
        subcategoria: 'Tradicionales',
        descripcion: 'Manjar de yemas perfumado a la vainilla coronado con merengue al oporto.',
        precio: 14.0,
        disponible: true,
        imagenUrl: 'https://images.unsplash.com/photo-1488477181946-6428a0291777?auto=format&fit=crop&w=300&q=80'
      }
    ];
  }

  private cargarProductosLocales(): Producto[] {
    try {
      const guardado = localStorage.getItem(STORAGE_PRODUCTOS);
      if (guardado) return JSON.parse(guardado);
    } catch {
      // Ignorar
    }
    return this.obtenerProductosIniciales();
  }

  private persistirLocal(lista: Producto[]): void {
    this.productos.set(lista);
    try {
      localStorage.setItem(STORAGE_PRODUCTOS, JSON.stringify(lista));
    } catch {
      // Ignorar
    }
  }

  listarProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(API_CONFIG.endpoints.productos).pipe(
      tap((datos) => {
        if (datos && datos.length > 0) {
          this.persistirLocal(datos);
        }
      }),
      catchError(() => of(this.productos()))
    );
  }

  crearProducto(nuevo: Omit<Producto, 'idProducto'>): Observable<Producto> {
    const { error, normalizado } = this.validarYNormalizar(nuevo);
    if (error || !normalizado) {
      return throwError(() => new Error(error));
    }

    return this.http.post<Producto>(API_CONFIG.endpoints.productos, normalizado).pipe(
      tap((creado) => this.persistirLocal([...this.productos(), creado])),
      catchError(() => {
        const actual = this.productos();
        const nuevoId = actual.length > 0 ? Math.max(...actual.map((p) => p.idProducto)) + 1 : 1;
        const prodLocal: Producto = {
          ...normalizado,
          idProducto: nuevoId
        };
        this.persistirLocal([...actual, prodLocal]);
        return of(prodLocal);
      })
    );
  }

  actualizarProducto(producto: Producto): Observable<Producto> {
    const { error, normalizado } = this.validarYNormalizar(producto);
    if (error || !normalizado) {
      return throwError(() => new Error(error));
    }

    const actualizado: Producto = {
      ...normalizado,
      idProducto: producto.idProducto
    };
    return this.http.put<Producto>(`${API_CONFIG.endpoints.productos}/${producto.idProducto}`, actualizado).pipe(
      tap(() => {
        this.persistirLocal(this.productos().map((p) => (p.idProducto === producto.idProducto ? actualizado : p)));
      }),
      catchError(() => {
        this.persistirLocal(this.productos().map((p) => (p.idProducto === producto.idProducto ? actualizado : p)));
        return of(actualizado);
      })
    );
  }

  eliminarProducto(idProducto: number): Observable<void> {
    return this.http.delete<void>(`${API_CONFIG.endpoints.productos}/${idProducto}`).pipe(
      tap(() => {
        this.persistirLocal(this.productos().filter((p) => p.idProducto !== idProducto));
      }),
      catchError(() => {
        this.persistirLocal(this.productos().filter((p) => p.idProducto !== idProducto));
        return of(undefined);
      })
    );
  }

  restaurarCatalogoBase(): void {
    this.persistirLocal(this.obtenerProductosIniciales());
  }
}
