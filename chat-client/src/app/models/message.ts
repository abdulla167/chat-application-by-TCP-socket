export interface Message {
  type: string,
  messageText: string,
  sendTo: string,
  sendFrom: string,
  date: Date,
  file?: string;
}
