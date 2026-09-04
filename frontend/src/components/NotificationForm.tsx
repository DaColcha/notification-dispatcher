import React, { useState } from 'react';
import { Send, Zap } from 'lucide-react';
import type {EventPayload} from "../types/event.ts";

const API_URL= 'http://localhost:8081/event';

export const NotificationForm = () => {
    const [channel, setChannel] = useState('DISCORD');
    const [destination, setDestination] = useState('da.colcha@gmail.com');
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        const payload : EventPayload = {
            eventId: crypto.randomUUID(),
            eventType: channel,
            destination: destination,
            message
        }

        try {
            await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    ...payload,
                    message: message || `Notificación de prueba [${channel}]`,
                }),
            });
            setMessage('');
        } catch (err) {
            console.error('Error enviando notificación:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleStressTest = async () => {
        setLoading(true);
        const channels = ['EMAIL', 'DISCORD', 'SLACK'];

        for (let i = 1; i <= 10; i++) {
            const randomChannel = channels[Math.floor(Math.random() * channels.length)];

            await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    eventId: crypto.randomUUID(),
                    eventType: randomChannel,
                    destination: destination,
                    message: `Mensaje de ráfaga #${i}: ${message}`}),
            });
        }
        setLoading(false);
    };

    return (
        <div className="bg-canvas-bg border border-canvas-border rounded-xl p-6 shadow-xl">
            <h2 className="text-xl font-semibold mb-4 text-canvas-text-bright flex items-center gap-2">
                <Send className="w-5 h-5 text-accent-primary" /> Envia un mensaje 
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-canvas-text-dim mb-1">Canal</label>
                    <select
                        value={channel}
                        onChange={(e) => setChannel(e.target.value)}
                        className="w-full bg-canvas-border border border-canvas-subtle rounded-lg p-2.5 text-canvas-text-bright focus:ring-2 focus:ring-indigo-500 outline-none"
                    >
                        <option value="DISCORD">Discord</option>
                        <option value="SLACK">Slack</option>
                        <option value="EMAIL">Email (Resend)</option>
                    </select>
                </div>

                {channel=="EMAIL" &&  (<div>
                    <label className="block text-sm font-medium text-canvas-text-dim mb-1">Email</label>
                    <input
                        value={destination}
                        onChange={(e) => setDestination(e.target.value)}
                        placeholder="Email destino"
                        className="w-full bg-canvas-border border border-canvas-subtle rounded-lg p-2.5 text-canvas-text-bright focus:ring-2 focus:ring-indigo-500 outline-none resize-none"
                    />
                </div>)}

                <div>
                    <label className="block text-sm font-medium text-canvas-text-dim mb-1">Mensaje</label>
                    <textarea
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        placeholder="Escribe el contenido del evento..."
                        className="w-full bg-canvas-border border border-canvas-subtle rounded-lg p-2.5 text-canvas-text-bright focus:ring-2 focus:ring-indigo-500 outline-none h-24 resize-none"
                    />
                </div>

                <div className="flex gap-3 pt-2">
                    <button
                        type="submit"
                        disabled={loading}
                        className="flex-1 bg-accent-strong hover:bg-indigo-500 text-white font-medium py-2.5 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
                    >
                        <Send className="w-4 h-4" /> Enviar Evento
                    </button>
                    <button
                        type="button"
                        onClick={handleStressTest}
                        disabled={loading}
                        className="bg-amber-600/20 hover:bg-amber-600/30 text-amber-400 border border-amber-500/30 font-medium py-2.5 px-4 rounded-lg transition-colors flex items-center gap-2"
                    >
                        <Zap className="w-4 h-4" /> Ráfaga (10x)
                    </button>
                </div>
            </form>
        </div>
    );
};