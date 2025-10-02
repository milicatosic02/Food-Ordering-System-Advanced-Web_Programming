import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Order} from "../model";

@Injectable({
  providedIn: 'root'
})
export class TrackOrderService {

  private readonly apiUrl = 'http://localhost:8080/api/orders/track';

  constructor(private http: HttpClient) {}

  getActiveOrders(): Observable<Order[]> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any>(this.apiUrl, {headers});
  }

}
