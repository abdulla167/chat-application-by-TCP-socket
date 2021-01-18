import {Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation} from '@angular/core';

const electron = require('electron');
const remote = electron.remote;

@Component({
  selector: 'app-chat-controls',
  templateUrl: './chat-controls.component.html',
  styleUrls: ['./chat-controls.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ChatControlsComponent implements OnInit {
  messageText: string;
  @Output()
  messageAdded: EventEmitter<string> = new EventEmitter<string>();

  @Output()
  fileSelected: EventEmitter<string> = new EventEmitter<string>();

  constructor() {
  }

  ngOnInit() {

  }

  addMessage() {
    console.log(this.messageText);
    this.messageAdded.emit(this.messageText);
    this.messageText = '';
  }

  selectFile() {
    remote.dialog.showOpenDialog({
      title: 'Select file',
      defaultPath : ".",
      buttonLabel: 'upload',
      properties: ['openFile']
    }).then(
      file =>{
        let filepath = file.filePaths[0].toString();
        this.fileSelected.emit(filepath);
      }
    );
  }
}
