export interface NotificationStatusUpdate {
    eventId: string;
    status: 'SUCCESS' | 'FAILED' | 'FAILED_DLQ' | 'RETRYING';
    eventType: string;
    detail: string;
    timestamp: number;
}
