import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { NavigationComponent } from './navigation/navigation.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { PersonComponent } from './navigation/person/person.component';
import {ChatHeaderComponent} from "./navigation/chat-header/chat-header.component";
import {ChatControlsComponent} from "./navigation/chat-controls/chat-controls.component";
import { DialogComponent } from './navigation/dailog/dialog.component';
import {FormsModule} from "@angular/forms";
import {MaterialModule} from "./material.module";
import { LoginRegisterComponent } from './login-register/login-register.component';
import {Route, RouterModule} from "@angular/router";
import { LoginComponent } from './login-register/login/login.component';
import { RegisterComponent } from './login-register/register/register.component';


const routes: Route[] = [
  {path: '', component: LoginRegisterComponent},
  {path: 'chat-app', component: NavigationComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    PersonComponent,
    ChatHeaderComponent,
    ChatControlsComponent,
    DialogComponent,
    LoginRegisterComponent,
    LoginComponent,
    RegisterComponent,
  ],
  imports: [
    BrowserAnimationsModule,
    MaterialModule,
    FormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
