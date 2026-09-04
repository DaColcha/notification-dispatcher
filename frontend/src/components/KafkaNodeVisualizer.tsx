import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {Server, Cpu} from 'lucide-react';
import type { Particle, EventType } from '../types/notification.ts';

interface NodeConfig {
    id: string;
    name: string;
    topic: string;
    channel: EventType;
}

interface VisualizerProps {
    particles: Particle[];
}

const NODES: NodeConfig[] = [
    { id: 'node-email', name: 'Email Consumer', topic: 'email.notification', channel: 'EMAIL' },
    { id: 'node-discord', name: 'Discord Consumer', topic: 'discord.notification', channel: 'DISCORD' },
    { id: 'node-slack', name: 'Slack Consumer', topic: 'slack.notification', channel: 'SLACK' },
];

export const KafkaClusterVisualizer: React.FC<VisualizerProps> = ({ particles }) => {
    return (
        <div className="bg-canvas-bg border border-canvas-border rounded-xl p-6 mb-6 text-white shadow-2xl">
            <div className="flex items-center justify-between mb-6">
                <h3 className="font-mono text-lg font-semibold flex items-center gap-2">
                    <Server className="w-5 h-5 text-stomp-glow" />
                    Live Kafka Partition Monitor
                </h3>
                <span className="text-xs bg-kafka-emerald/10 text-emerald-400 border border-kafka-emerald/20 px-3 py-1 rounded-full font-mono">
          STOMP Connected
        </span>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {NODES.map((node) => {
                    const nodeParticles = particles.filter((p) => p.targetNodeId === node.id);

                    return (
                        <div
                            key={node.id}
                            className="bg-canvas-border/60 border border-canvas-subtle/80 rounded-lg p-4 min-h-[180px] flex flex-col justify-between"
                        >
                            <div>
                                <div className="flex items-center justify-between mb-2">
                                    <span className="font-mono text-sm text-canvas-text-muted font-bold">{node.name}</span>
                                    <Cpu className="w-4 h-4 text-canvas-text-dark" />
                                </div>
                                <span className="text-xs font-mono text-canvas-text-dark block mb-4">{node.topic}</span>
                            </div>

                            <div className="grid grid-rows-3 gap-3">
                                {[0, 1, 2].map((partitionIdx) => {
                                    const partitionParticles = nodeParticles.filter(
                                        (p) => p.partition === partitionIdx
                                    );

                                    return (
                                        <div
                                            key={partitionIdx}
                                            className="relative h-13 bg-canvas-bg/80 border border-kafka-emerald/30 rounded-lg flex items-center justify-between px-3 overflow-hidden"
                                        >
                                          <span className="text-[11px] font-mono text-emerald-400 font-semibold z-10">
                                            P-{partitionIdx}
                                          </span>

                                            {partitionParticles.length > 0 && (
                                                <div className="absolute inset-0 bg-kafka-emerald/20 animate-pulse" />
                                            )}

                                            <AnimatePresence>
                                                {partitionParticles.map((particle) => {
                                                    const isFailed = particle.status === 'FAILED';
                                                    const bgColor = isFailed ? 'bg-red-400' : 'bg-blue-600';
                                                    return (
                                                    <motion.div
                                                        key={particle.id}
                                                        initial={{ scale: 0, opacity: 0, x: -30 }}
                                                        animate={{ scale: 1, opacity: 1, x: 0 }}
                                                        exit={{ scale: 0, opacity: 0, x: 20 }}
                                                        transition={{ duration: 1.5, ease: 'backOut' }}
                                                        className={"flex items-center px-0.5 py-0.5 rounded text-[10px] font-mono text-white shadow-[0_0_10px_#3b82f6] z-10 " + bgColor}
                                                    >
                                                        <span className="w-1.5 h-1.5 bg-white rounded-full animate-ping" />
                                                    </motion.div>
                                                )})}
                                            </AnimatePresence>
                                        </div>
                                    );
                                })}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};