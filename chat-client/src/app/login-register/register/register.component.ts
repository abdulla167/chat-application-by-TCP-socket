import {Component, NgZone, OnInit} from '@angular/core';
import {AppProtocolService} from "../../services/app-protocol.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  username: string;
  phone: string;
  password: string;

  hintError = false;

  constructor(private appProtocolService: AppProtocolService, private ngZone: NgZone, private router: Router) { }

  ngOnInit(): void {
    this.appProtocolService.registerEndpoint.subscribe(data => {
      this.ngZone.run(()=>{
        if(data.response == false) {
          this.hintError =true;
        } else {
          this.router.navigateByUrl('/chat-app', {state: {friends: [], user: data.user}});
        }
      })
    })
  }

  register() {
    console.log("register");
    this.appProtocolService.register({
      username: this.username,
      phone: this.phone,
      password: this.password
    })
  }
}
