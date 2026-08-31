import React, { useState } from 'react';
import { Send, Zap } from 'lucide-react';
import type {EventPayload} from "../types/event.ts";

const API_URL= 'http://localhost:8081/event';

export const NotificationForm = () => {
    const [channel, setChannel] = useState('DISCORD');
    const [destination, setDestination] = useState('DEFAULT');
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        const payload : EventPayload = {
            eventId: crypto.randomUUID(),
            eventType: channel,
            destination: "da.colcha@gmail.com",
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

    // Simulación de ráfaga para probar la capacidad de Kafka y los Consumers
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
                    destination: "da.colcha@gmail.com",
                    message: `Mensaje de ráfaga #${i} en batch test`}),
            });
        }
        setLoading(false);
    };

    return (
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl">
            <h2 className="text-xl font-semibold mb-4 text-slate-100 flex items-center gap-2">
                <Send className="w-5 h-5 text-indigo-400" /> Dispatcher Center
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-slate-400 mb-1">Canal</label>
                    <select
                        value={channel}
                        onChange={(e) => setChannel(e.target.value)}
                        className="w-full bg-slate-800 border border-slate-700 rounded-lg p-2.5 text-slate-100 focus:ring-2 focus:ring-indigo-500 outline-none"
                    >
                        <option value="DISCORD">Discord</option>
                        <option value="SLACK">Slack</option>
                        <option value="EMAIL">Email (Resend)</option>
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-slate-400 mb-1">Mensaje</label>
                    <textarea
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        placeholder="Escribe el contenido del evento..."
                        className="w-full bg-slate-800 border border-slate-700 rounded-lg p-2.5 text-slate-100 focus:ring-2 focus:ring-indigo-500 outline-none h-24 resize-none"
                    />
                </div>

                <div className="flex gap-3 pt-2">
                    <button
                        type="submit"
                        disabled={loading}
                        className="flex-1 bg-indigo-600 hover:bg-indigo-500 text-white font-medium py-2.5 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
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