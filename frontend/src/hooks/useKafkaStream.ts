import { useState, useCallback } from 'react';
import type {Particle, EventType, NotificationStatusUpdate} from '../types/notification.ts';

export const useKafkaStream = () => {
    const [particles, setParticles] = useState<Particle[]>([]);

    const spawnParticle = useCallback((eventData: NotificationStatusUpdate) => {
        const nodeMapping: Record<EventType, string> = {
            EMAIL: 'node-email',
            DISCORD: 'node-discord',
            SLACK: 'node-slack',
        };

        const targetNodeId = nodeMapping[eventData.eventType] || 'node-email';
        const particleId = `${eventData.eventId}-${Date.now()}`;

        const assignedPartition = eventData.partition !== undefined
            ? eventData.partition
            : Math.floor(Math.random() * 3);

        const newParticle: Particle = {
            id: particleId,
            targetNodeId,
            status: eventData.status,
            detail: eventData.detail,
            partition: assignedPartition
        };

        setParticles((prev) => [...prev, newParticle]);

        setTimeout(() => {
            setParticles((prev) => prev.filter((p) => p.id !== particleId));
        }, 1500);
    }, []);

    return { particles, spawnParticle };
};