class WebSocketService {
  static instance: null | WebSocketService = null;
  reconnectAttempts = 0;
  maxReconnectAttempts = 5;
  socket: null | WebSocket = null

  static getInstance() {
    if (!WebSocketService.instance) {
      WebSocketService.instance = new WebSocketService();
    }
    return WebSocketService.instance;
  }

  constructor() {
    this.socket = null;
  }

  connect(url: string) {
    this.socket = new WebSocket(url);

    // 连接时触发
    this.socket.onopen = () => {
      console.log("WebSocket 已连接");
      this.reconnectAttempts = 0;
    };

    // 收到消息时触发
    this.socket.onmessage = (event) => {
      this.handleMessage(event.data)
      // const parsedData = JSON.parse(event.data.toString());
      // console.log("[服务器]" + parsedData.content)

    };

    // 连接断开时触发
    this.socket.onclose = () => {
      console.log("WebSocket 断开连接");
      this.handleReconnect(url);
    };

    // 出现错误时触发
    this.socket.onerror = (err) => {
      console.error("WebSocket 错误:", err);
      this.socket?.close();
    };
  }

  handleMessage(data){
    console.log(1111)
  }

  handleReconnect(url: string) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      setTimeout(() => {
        this.reconnectAttempts++;
        console.log(`尝试重新连接 (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
        this.connect(url);
      }, 3000);
    }
  }

  // 发送消息
  sendMessage(msg: string, email: string) {
    const message = JSON.stringify({
      role: "user",
      sendEmail: email,
      content: msg,
    });
    if (this.socket?.readyState === WebSocket.OPEN) {
      console.log("[客户端]:" + message)
      this.socket?.send(message);
    } else {
      console.error("WebSocket 未连接");
    }
  }

  // 关闭连接
  disconnect() {
    this.socket?.close();
    this.reconnectAttempts = this.maxReconnectAttempts;
  }
}

export default WebSocketService.getInstance();
