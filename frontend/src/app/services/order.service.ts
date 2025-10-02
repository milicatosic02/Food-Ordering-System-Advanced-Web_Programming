import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import {Order} from "../model";

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  createOrder(order: Order): Observable<any> {

    console.log("Creating order with data:", order);

    const token = localStorage.getItem('jwt'); // Preuzimanje tokena iz localStorage

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(`${this.apiUrl}/place`, order, { headers });
  }

  scheduleOrder(orderId: string, scheduleTime: string): Observable<any> {
    const token = localStorage.getItem('jwt'); // Preuzimanje tokena iz localStorage

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(`${this.apiUrl}/${orderId}/schedule`, { scheduleTime }, { headers });
  }

  cancelOrder(order: Order):  Observable<string>  {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`); // Dodavanje zaglavlja

    return this.http.put(`${this.apiUrl}/${order.id}/cancel`, null, { headers, responseType: 'text' });
  }

}
