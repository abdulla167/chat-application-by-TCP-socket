import {Component, NgZone, OnInit} from '@angular/core';
import {AppProtocolService} from "../../services/app-protocol.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  phone: string;
  password: string;

  hintError = false;
  constructor(private appProtocolService: AppProtocolService, private ngZone: NgZone, private router: Router) {}

  ngOnInit(): void {
    this.appProtocolService.loginEndpoint.subscribe(data => {
      this.ngZone.run(()=>{
        if(data.response == false) {
          this.hintError =true;
        } else {
          this.router.navigateByUrl('/chat-app', {state: {friends: data.friends, user: data.user}});
        }
      })
    })
  }

  login() {
    this.appProtocolService.login(this.phone, this.password);
  }
}
