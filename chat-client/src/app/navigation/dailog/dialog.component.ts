import {Component, Inject, NgZone, OnInit} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from "@angular/material/dialog";
import {AppProtocolService} from "../../services/app-protocol.service";
import {Friend} from "../../models/friend";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent implements OnInit {

  spin = false;
  showError = false;

  constructor(
    public dialogRef: MatDialogRef<DialogComponent, Friend>,
    @Inject(MAT_DIALOG_DATA) public data: string,
    private appProtocol: AppProtocolService,
    private ngZone: NgZone
  ) {
  }

  ngOnInit(): void {
    this.appProtocol.addFriendEndpoint.subscribe(data => {
      this.ngZone.run(()=> {
        if(data.response === true) {
          this.dialogRef.close(data.payload);
        } else {
          this.showError = true;
        }
        this.spin =false;
      });
    });
  }

  onCancelClick() {
    this.dialogRef.close();
  }

  onAddClick() {
    this.appProtocol.addFriend(this.data);
    this.spin =true;
  }
}
