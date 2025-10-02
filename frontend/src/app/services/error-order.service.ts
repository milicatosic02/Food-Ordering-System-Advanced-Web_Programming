import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {ErrorMessage, Order} from "../model";

@Injectable({
  providedIn: 'root'
})
export class ErrorOrderService {

  private readonly apiUrl = 'http://localhost:8080/api/errors';

  constructor(private http: HttpClient) {}

  getErrorOrders(page: number, size: number): Observable<ErrorMessage[]> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ErrorMessage[]>(this.apiUrl, { headers, params });
  }
}
