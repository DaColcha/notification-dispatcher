import { useNotificationSocket } from './hooks/useNotificationSocket';
import { NotificationForm } from './components/NotificationForm';
import { Activity, CheckCircle2, AlertTriangle, XCircle, Radio } from 'lucide-react';
import {useEffect} from "react";
import {KafkaClusterVisualizer} from "./components/KafkaNodeVisualizer.tsx";
import {useKafkaStream} from "./hooks/useKafkaStream.ts";

function App() {
  const { updates, isConnected } = useNotificationSocket();
  const { particles, spawnParticle } = useKafkaStream();
  const { lastMessage } = useNotificationSocket();

  useEffect(() => {
    // Cuando entra una notificación por WebSocket STOMP
    if (lastMessage) {
      spawnParticle(lastMessage);
    }
  }, [lastMessage, spawnParticle]);

  return (
      <div className="min-h-screen p-8">
        <header className="max-w-6xl mx-auto flex justify-between items-center mb-8 pb-4 border-b border-canvas-border">
          <div>
            <h1 className="text-3xl font-bold bg-accent-primary bg-clip-text text-transparent">
              Notification Dispatcher
            </h1>
            <p className="text-canvas-text-main text-sm">Real-time Notification Hub Visualizer</p>
          </div>
          <div className="flex items-center gap-2 bg-canvas-card border border-canvas-border text-canvas-text-dim px-3 py-1.5 rounded-full text-sm">
            <Radio className={`w-4 h-4 ${isConnected ? 'text-emerald-400 animate-pulse' : 'text-rose-500'}`} />
            <span>{isConnected ? 'WebSocket Conectado' : 'Desconectado'}</span>
          </div>
        </header>

        <main className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-1">
            <NotificationForm />
          </div>

          <div className="lg:col-span-2 bg-canvas-bg border border-canvas-border rounded-xl p-6 shadow-xl flex flex-col h-[600px]">
            <KafkaClusterVisualizer particles={particles} />
            <h2 className="text-xl font-semibold mb-4 text-canvas-text-bright flex items-center gap-2">
              <Activity className="w-5 h-5 text-accent-primary" /> Real-time Consumer Stream
            </h2>

            <div className="flex-1 overflow-y-auto space-y-3 pr-2 custom-scrollbar">
              {updates.length === 0 ? (
                  <div className="h-full flex flex-col items-center justify-center text-canvas-text-dark">
                    <Activity className="w-12 h-12 mb-2 stroke-1" />
                    <p>Esperando eventos desde Kafka...</p>
                  </div>
              ) : (
                  updates.map((update, idx) => (
                      <div
                          key={`${update.eventId}-${idx}`}
                          className="bg-canvas-border/60 border border-canvas-subtle/50 p-4 rounded-lg flex items-start justify-between gap-4 transition-all animate-fadeIn"
                      >
                        <div className="flex items-start gap-3">
                          {update.status === 'SUCCESS' && <CheckCircle2 className="w-5 h-5 text-emerald-400 mt-0.5" />}
                          {update.status === 'FAILED' && <AlertTriangle className="w-5 h-5 text-amber-400 mt-0.5" />}
                          {update.status === 'RETRYING' && <XCircle className="w-5 h-5 text-rose-500 mt-0.5" />}
                          <div>
                            <div className="flex items-center gap-2">
                        <span className="font-mono text-xs px-2 py-0.5 bg-canvas-subtle rounded text-canvas-text-muted">
                          {update.eventType}
                        </span>
                              <span className="text-xs font-mono text-canvas-text-dim">{update.eventId}</span>
                            </div>
                            <p className="text-sm text-slate-200 mt-1">{update.detail}</p>
                          </div>
                        </div>
                        <span className="text-[10px] text-canvas-text-dark font-mono">
                    {new Date(update.timestamp).toLocaleTimeString()}
                  </span>
                      </div>
                  ))
              )}
            </div>
          </div>
        </main>
      </div>
  );
}
export default App
