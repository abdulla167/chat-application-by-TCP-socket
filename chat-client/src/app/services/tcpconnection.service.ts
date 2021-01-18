import {Injectable} from '@angular/core';
import {TcpSocketConnectOpts, Socket} from 'net';

@Injectable({
  providedIn: 'root'
})
export class TCPConnectionService {
  private socket: Socket;

  constructor() {}

  connect(options: TcpSocketConnectOpts, listener: () => void): void {
    this.socket = new Socket();
    this.socket.connect(options, listener);
    this.socket?.on('end', () => {
      console.log('Requested an end to the TCP connection');
    });
    this.socket.on('timeout',()=>{
      console.log('Socket timed out !');
    });
  }

  onData(listener: (data: Buffer) => void): void {
    this.socket?.on('data', listener);
  }

  sendData(data: any): void {
    console.log(data);
    this.socket?.write(data + "\n");
  }
  isConnected(): boolean {
    if (this.socket != null)
      return this.socket.connecting
    return false;
  }

  closeConnection(): void {
    this.socket?.end();
  }
}
