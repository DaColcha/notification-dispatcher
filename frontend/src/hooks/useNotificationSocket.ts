import { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type {NotificationStatusUpdate} from "../types/notification.ts";


export const useNotificationSocket = () => {
    const [updates, setUpdates] = useState<NotificationStatusUpdate[]>([]);
    const [isConnected, setIsConnected] = useState(false);

    useEffect(() => {
        const socket = new SockJS('http://localhost:8085/ws');
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            debug: (str) => console.log('🔍 [STOMP Debug]:', str),
            onConnect: () => {
                setIsConnected(true);
                console.log('✅ Conectado a WebSockets');

                client.subscribe('/event/updates', (message) => {
                    console.log(message);
                    if (message.body) {
                        const data: NotificationStatusUpdate = JSON.parse(message.body);
                        setUpdates((prev) => [data, ...prev.slice(0, 49)]); // Guardar las últimas 50
                    }
                });
            },
            onDisconnect: () => {
                setIsConnected(false);
                console.log('❌ Desconectado de WebSockets');
            },
        });

        client.activate();

        return () => {
            client.deactivate();
        };
    }, []);

    return { updates, isConnected };
};