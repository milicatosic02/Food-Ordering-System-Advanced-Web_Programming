import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { OrderService } from "../../services/order.service";
import {Order} from "../../model";

import { Router } from '@angular/router';
import * as moment from 'moment-timezone';



@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  orderForm: FormGroup;
  scheduleForm: FormGroup;
  cartItems: any[] = [];
  responseMessage: string = '';
  scheduleResponse: string = '';
  isScheduleModalOpen: boolean = false;
  scheduledDate: string = '';
  finalPrice: number;

  isOrderSuccess: number = 0;


  constructor(private fb: FormBuilder, private orderService: OrderService, private router: Router) {

    this.orderForm = this.fb.group({
      address: ['', Validators.required],
      paymentMethod: ['', Validators.required],
      items: this.fb.array([]),

    });

    // Schedule form initialization
    this.scheduleForm = this.fb.group({
      orderId: ['', Validators.required],
      scheduleTime: ['', Validators.required],
    });

    this.finalPrice = 0;
  }

  ngOnInit(): void {
    this.loadCartItems();
    this.finalPrice = this.calculateFinalPrice();
    //this.loadOrderForm();
  }

  // Getter for items array
  get items(): FormArray {
    return this.orderForm.get('items') as FormArray;
  }

  // Load items from localStorage into the order form
  loadCartItems(): void {
    this.cartItems = JSON.parse(localStorage.getItem('cartItems') || '[]');
    if (this.cartItems.length > 0) {
      this.cartItems.forEach(item => {
        this.addItem(item);  // Add each item from the cart into the order form
      });
    }
  }

  // Add item from cart into the order form
  addItem(item: any): void {
    this.items.push(
      this.fb.group({
        productId: [item.name, Validators.required],
        quantity: [item.quantity, [Validators.required, Validators.min(1)]],
      })
    );
  }

  // Method to remove an item or reduce quantity
  removeItem(index: number): void {
    const item = this.cartItems[index];

    if (item.quantity > 1) {
      item.quantity -= 1;  // Reduce the quantity by 1
    } else {
      this.cartItems.splice(index, 1);  // Remove the item completely
    }

    this.items.removeAt(index);  // Remove the item from FormArray
    localStorage.setItem('cartItems', JSON.stringify(this.cartItems));  // Update localStorage

    this.finalPrice -= item.cena;
  }

  // Method to submit a new order
  submitOrder(): void {
    if (this.orderForm.valid) {
      const order: Order = {
        address: this.orderForm.value.address,
        paymentType: this.orderForm.value.paymentMethod,
        scheduledFor: this.scheduledDate ? new Date(this.scheduledDate) : undefined,
        dishes: this.cartItems.flatMap(item =>
          Array.from({ length: item.quantity }).map(() => ({ id: item.id }))
        ),
      };

      console.log("Zakazano vreme za slanje: " + this.scheduledDate)
      this.orderService.createOrder(order).subscribe({
        next: () => {
          this.orderForm.reset();
          this.items.clear();
          this.cartItems = [];
          localStorage.setItem('cartItems', JSON.stringify([]));


          this.isOrderSuccess = 1;
        },
        error: (errorResponse) => {
          if (errorResponse.status === 403) {
            let errorMessage = 'Niste autorizovani za zeljenu aktivnost!';
            this.isOrderSuccess = -2;
          } else {
            this.isOrderSuccess = -1;
            let errorMessage = 'Sorry, your order can\'t be created because our capacity is full currently, try again later!';
            if (errorResponse.status === 400) {
              errorMessage = errorResponse.error || errorMessage;
            }
          }
        }
      });
    } else {
      this.responseMessage = 'Please fill in all required fields.';
    }
  }


  // Method to open schedule modal
  openScheduleModal(): void {
    this.isScheduleModalOpen = true;
  }

  closeAlert(): void {
    this.isOrderSuccess = 0;
    this.orderForm.reset();
    this.items.clear();
    this.cartItems = [];
    localStorage.setItem('cartItems', JSON.stringify([]));

    this.router.navigate(['/dishes']); // Preusmeravanje na /home
  }


  closeScheduleModal(): void {
    this.isScheduleModalOpen = false;
  }

  confirmSchedule(): void {
    if (this.scheduledDate) {
      this.submitOrder();
      this.closeScheduleModal();
    } else {
      this.scheduleResponse = 'Please select a date and time to schedule the order.';
    }
  }

  calculateFinalPrice(): number{
    if (this.cartItems.length > 0) {
      this.cartItems.forEach(item => {
          this.finalPrice += item.cena*item.quantity;
      });
    }
    else
      this.finalPrice = 0;

    return this.finalPrice;
  }


}
