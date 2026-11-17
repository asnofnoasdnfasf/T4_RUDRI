import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { RudriHeaderComponent } from '../../components/rudri-header/rudri-header.component';
import { CantidadStepperComponent } from '../../components/cantidad-stepper/cantidad-stepper.component';
import { PedidoService } from '../../services/pedido.service';
import { Producto } from '../../models/producto.model';

@Component({
  selector: 'app-confirmacion',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    RudriHeaderComponent,
    CantidadStepperComponent
  ],
  templateUrl: './confirmacion.component.html',
  styleUrl: './confirmacion.component.css'
})
export class ConfirmacionComponent {
  readonly pedidoService = inject(PedidoService);

  readonly mensajeExito = signal<string>('');
  readonly mensajeError = signal<string>('');

  cambiarCantidad(producto: Producto, nuevaCantidad: number): void {
    const res = this.pedidoService.actualizarCantidadProducto(producto, nuevaCantidad);
    if (!res.exito) {
      this.mensajeError.set(res.mensaje);
    } else {
      this.mensajeError.set('');
    }
  }

  actualizarObservacion(texto: string): void {
    this.pedidoService.observacionGeneral.set(texto);
  }

  enviarPedido(): void {
    this.mensajeError.set('');
    this.mensajeExito.set('');

    this.pedidoService.enviarPedidoACocina().subscribe({
      next: (pedidoCreado) => {
        const mesaFmt = String(pedidoCreado.numeroMesa).padStart(2, '0');
        this.mensajeExito.set(
          `¡Pedido #${pedidoCreado.codigo} (Mesa ${mesaFmt}) enviado a cocina exitosamente!`
        );
      },
      error: (err: Error) => {
        this.mensajeError.set(err.message || 'No se pudo enviar el pedido.');
      }
    });
  }
}
