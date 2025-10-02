import { Component, OnInit } from '@angular/core';
import { ErrorMessage } from "../../model";
import { ErrorOrderService } from "../../services/error-order.service";
import { ChangeDetectorRef } from '@angular/core';


@Component({
  selector: 'app-error-order',
  templateUrl: './error-order.component.html',
  styleUrls: ['./error-order.component.css']
})
export class ErrorOrderComponent implements OnInit {
  errors: ErrorMessage[] = [];
  currentPage: number = 0; // Trenutna stranica
  pageSize: number = 3; // Broj elemenata po stranici
  totalPages: number = 0; // Ukupan broj stranica

  constructor(private errorOrderService: ErrorOrderService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadErrorOrders(); // Učitaj podatke prilikom inicijalizacije
  }

  // Metoda za učitavanje grešaka sa paginacijom
  loadErrorOrders(): void {
    console.log('Loading page:', this.currentPage, 'with size:', this.pageSize);

    this.errorOrderService.getErrorOrders(this.currentPage, this.pageSize).subscribe({
      next: (response: any) => {
        console.log('Response:', response);
        this.errors = response.content; // Podaci sa trenutne stranice
        this.totalPages = response.totalPages; // Ukupan broj stranica
        console.log('Provera odgovora od servera Errors loaded for page', this.currentPage, ':', this.errors);
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Error fetching error orders:', err);
      }
    });
  }

  // Navigacija na sledeću stranicu
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      console.log('Navigating to page:', this.currentPage); // Provera
      this.loadErrorOrders();
    }
  }

  // Navigacija na prethodnu stranicu
  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      console.log('Navigating to page:', this.currentPage); // Provera
      this.loadErrorOrders();
    }
  }
}
