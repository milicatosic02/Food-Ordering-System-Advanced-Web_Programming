import {Component, OnInit} from '@angular/core';
import {Order} from "../../model";
import {HistoryService} from "../../services/history.service";
import {TrackOrderService} from "../../services/track-order-service";
import {OrderService} from "../../services/order.service";
import {CompatClient, Stomp} from "@stomp/stompjs";
import * as SockJS from 'sockjs-client';
import {HttpHeaders} from "@angular/common/http";


@Component({
  selector: 'app-track-order',
  templateUrl: './track-order.component.html',
  styleUrls: ['./track-order.component.css']
})
export class TrackOrderComponent implements OnInit{

  orders: Order[] = [];
  //@ts-ignore
  stompClient: CompatClient;
  isCancellationSuccess: number = 0;
  constructor(private trackOrderService: TrackOrderService, private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadActiveOrders();
    this.initializeWebSocketConnection();
  }

  loadActiveOrders(): void {
    this.trackOrderService.getActiveOrders().subscribe({
      next: (data: Order[]) => {
        this.orders = data;
        console.log('Active ordres loaded:', data);
      },
      error: (err: any) => {
        console.error('Error fetching order history:', err);
      }
    });
  }

  initializeWebSocketConnection(): void {
    const jwt = localStorage.getItem('jwt');

    const socket = SockJS(`http://localhost:8080/ws?jwt=${jwt}`);
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect({}, this.onConnect.bind(this));
  }

  onConnect(frame: any): void {

    // Pretplata na kanal '/topic/order-status' za primanje poruka o statusu porudžbina
    this.stompClient?.subscribe('/topic/order-status', (message: any) => {

          const parsedMessage = JSON.parse(message.body);
          // Provera da li poruka ima orderId i status
          if (parsedMessage.orderId && parsedMessage.status) {
            const {orderId, status} = parsedMessage;

            const order = this.orders.find(order => order.id === orderId);
            if (order) {
              order.status = status;
              console.log('Ažuriran status porudžbine:', order);
            }
      }
    });
  }


  cancelOrder(order: Order): void {
   // const confirmCancel = confirm(`Are you sure you want to cancel the order with ID ${order.id}?`);

  //  if (confirmCancel) {
      this.orderService.cancelOrder(order).subscribe({
        next: (response: string) => {
          // Uklanjamo otkazanu porudžbinu iz liste
          this.orders = this.orders.filter(o => o.id !== order.id);
          console.log(`Order with ID ${order.id} canceled successfully.`);
          console.log(response);
          this.isCancellationSuccess = 1;
        },
        error: (err: any) => {
          console.error(`Failed to cancel order with ID ${order.id}:`, err);
          console.log("error::: " + err)
          this.isCancellationSuccess = -1;
          //alert('The order cannot be canceled. It may have already been processed or delivered.');
        },
      });
   // }
  }


  closeAlert(): void {
    this.isCancellationSuccess = 0;

  }

}
