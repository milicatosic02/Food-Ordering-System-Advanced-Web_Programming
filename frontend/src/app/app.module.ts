import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LoginComponent} from "./components/login/login.component";
import {UserCreateComponent} from "./components/users/user-create/user-create.component";
import {UserEditComponent} from "./components/users/user-edit/user-edit.component";
import {UserListComponent} from "./components/users/user-list/user-list.component";
import {HttpClientModule} from "@angular/common/http";
import { AppComponent } from './components/app/app.component';
import { AuthService } from './services/auth.service';
import { DishComponent } from './components/dish/dish.component';
import { CartComponent } from './components/cart/cart.component';
import { HistoryComponent } from './components/history/history.component';
import { ErrorOrderComponent } from './components/error-order/error-order.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TrackOrderComponent } from './components/track-order/track-order.component';



@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    UserCreateComponent,
    UserEditComponent,
    UserListComponent,
    DishComponent,
    CartComponent,
    HistoryComponent,
    ErrorOrderComponent,
    TrackOrderComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
