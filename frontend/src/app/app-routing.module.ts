import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {UserListComponent} from "./components/users/user-list/user-list.component";
import {UserEditComponent} from "./components/users/user-edit/user-edit.component";
import {UserCreateComponent} from "./components/users/user-create/user-create.component";
import {DishComponent} from "./components/dish/dish.component";
import {CartComponent} from "./components/cart/cart.component";
import {HistoryComponent} from "./components/history/history.component";
import {ErrorOrderComponent} from "./components/error-order/error-order.component";
import {TrackOrderComponent} from "./components/track-order/track-order.component";
import {AuthGuard} from "./guards/auth.guard";


const routes: Routes = [

  {
    path: "",
    redirectTo: "login",
    pathMatch: "full"
  },

  {
    path: "login",
    component: LoginComponent
  },

  {
    path: "dishes",
    component: DishComponent,
     canActivate: [AuthGuard]
  },

  {
    path: "users",
    component: UserListComponent,
    runGuardsAndResolvers: 'always',
    canActivate: [AuthGuard]

  },

  {
    path: "edit/:id",
   component: UserEditComponent,
  },

  {
    path: "add",
    component: UserCreateComponent
  },

  {
    path: "cart",
    component: CartComponent,
    canActivate: [AuthGuard]
  },

  {
    path: "history",
    component: HistoryComponent,
    canActivate: [AuthGuard]
  },

  {
    path: "errors",
    component: ErrorOrderComponent,
    canActivate: [AuthGuard]
  },

  {
    path: "track",
    component: TrackOrderComponent,
    canActivate: [AuthGuard]
  },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
