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
      <div className="min-h-screen bg-slate-950 text-slate-100 p-8">
        <header className="max-w-6xl mx-auto flex justify-between items-center mb-8 pb-4 border-b border-slate-800">
          <div>
            <h1 className="text-3xl font-bold bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">
              DispatchPulse
            </h1>
            <p className="text-slate-400 text-sm">Real-time Event-Driven Notification Hub</p>
          </div>
          <div className="flex items-center gap-2 bg-slate-900 border border-slate-800 px-3 py-1.5 rounded-full text-sm">
            <Radio className={`w-4 h-4 ${isConnected ? 'text-emerald-400 animate-pulse' : 'text-rose-500'}`} />
            <span>{isConnected ? 'WebSocket Conectado' : 'Desconectado'}</span>
          </div>
        </header>

        <main className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-1">
            <NotificationForm />
          </div>

          <div className="lg:col-span-2 bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl flex flex-col h-[600px]">
            <KafkaClusterVisualizer particles={particles} />
            <h2 className="text-xl font-semibold mb-4 text-slate-100 flex items-center gap-2">
              <Activity className="w-5 h-5 text-indigo-400" /> Real-time Consumer Stream
            </h2>

            <div className="flex-1 overflow-y-auto space-y-3 pr-2 custom-scrollbar">
              {updates.length === 0 ? (
                  <div className="h-full flex flex-col items-center justify-center text-slate-500">
                    <Activity className="w-12 h-12 mb-2 stroke-1" />
                    <p>Esperando eventos desde Kafka...</p>
                  </div>
              ) : (
                  updates.map((update, idx) => (
                      <div
                          key={`${update.eventId}-${idx}`}
                          className="bg-slate-800/60 border border-slate-700/50 p-4 rounded-lg flex items-start justify-between gap-4 transition-all animate-fadeIn"
                      >
                        <div className="flex items-start gap-3">
                          {update.status === 'SUCCESS' && <CheckCircle2 className="w-5 h-5 text-emerald-400 mt-0.5" />}
                          {update.status === 'FAILED' && <AlertTriangle className="w-5 h-5 text-amber-400 mt-0.5" />}
                          {update.status === 'RETRYING' && <XCircle className="w-5 h-5 text-rose-500 mt-0.5" />}
                          <div>
                            <div className="flex items-center gap-2">
                        <span className="font-mono text-xs px-2 py-0.5 bg-slate-700 rounded text-slate-300">
                          {update.eventType}
                        </span>
                              <span className="text-xs font-mono text-slate-400">{update.eventId}</span>
                            </div>
                            <p className="text-sm text-slate-200 mt-1">{update.detail}</p>
                          </div>
                        </div>
                        <span className="text-[10px] text-slate-500 font-mono">
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
