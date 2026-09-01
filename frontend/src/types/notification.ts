export type NotificationStatus = 'SUCCESS' | 'FAILED' | 'RETRYING';
export type EventType = 'EMAIL' | 'DISCORD' | 'SLACK';

export interface NotificationStatusUpdate {
    eventId: string;
    status: NotificationStatus;
    eventType: EventType;
    detail: string;
    timestamp: number;
    partition: number;
}

export interface Particle {
    id: string;
    targetNodeId: string;
    status: NotificationStatus;
    detail: string;
    partition: number;
}