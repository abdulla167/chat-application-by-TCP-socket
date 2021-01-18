import {Message} from "./message";

export interface MessageEndpoint{
  endpoint: string,
  payload: Message
}

export interface MessageReceive {
  endpoint: string,
  payload: Message;
}
