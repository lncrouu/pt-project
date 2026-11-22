import java.util.*;

/**
 * BATANGAS STATE UNIVERSITY - CICS ALANGILAN
 * PT 101: PLATFORM TECHNOLOGIES FINAL PROJECT
 * TOPIC 2: CPU SCHEDULING SIMULATOR BACKEND ENGINE
 */
public class CPUSchedulerBackend {

    public static class Process {
        public String id;
        public int arrivalTime;
        public int burstTime;
        public int remainingTime;
        public int completionTime;
        public int turnaroundTime;
        public int waitingTime;
        public int responseTime;
        public int firstStartTime = -1;
        public boolean completed = false;
        public int inputIndex; // Maintain numerical input order (P1, P2, P3... P10)

        public Process(String id, int arrivalTime, int burstTime, int inputIndex) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
            this.inputIndex = inputIndex;
        }

        public Process copy() {
            Process p = new Process(this.id, this.arrivalTime, this.burstTime, this.inputIndex);
            p.remainingTime = this.remainingTime;
            p.completionTime = this.completionTime;
            p.turnaroundTime = this.turnaroundTime;
            p.waitingTime = this.waitingTime;
            p.responseTime = this.responseTime;
            p.firstStartTime = this.firstStartTime;
            p.completed = this.completed;
            return p;
        }
    }

    public static class GanttBlock {
        public String processId;
        public int startTime;
        public int endTime;

        public GanttBlock(String processId, int startTime, int endTime) {
            this.processId = processId;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public static class SimulationResult {
        public String algorithmName;
        public int timeQuantum;
        public List<Process> processes;
        public List<GanttBlock> ganttChart;
        public double avgCompletionTime;
        public double avgTurnaroundTime;
        public double avgWaitingTime;
        public double avgResponseTime;

        public SimulationResult(String algorithmName, int timeQuantum, List<Process> processes, List<GanttBlock> ganttChart) {
            this.algorithmName = algorithmName;
            this.timeQuantum = timeQuantum;
            this.processes = processes;
            this.ganttChart = ganttChart;
            calculateAverages();
        }

        private void calculateAverages() {
            if (processes == null || processes.isEmpty()) return;
            double totalCT = 0, totalTAT = 0, totalWT = 0, totalRT = 0;
            for (Process p : processes) {
                totalCT += p.completionTime;
                totalTAT += p.turnaroundTime;
                totalWT += p.waitingTime;
                totalRT += p.responseTime;
            }
            int n = processes.size();
            this.avgCompletionTime = totalCT / n;
            this.avgTurnaroundTime = totalTAT / n;
            this.avgWaitingTime = totalWT / n;
            this.avgResponseTime = totalRT / n;
        }
    }

    public static SimulationResult runFCFS(List<Process> inputList) {
        List<Process> procs = deepCopy(inputList);
        procs.sort((p1, p2) -> {
            if (p1.arrivalTime != p2.arrivalTime) {
                return Integer.compare(p1.arrivalTime, p2.arrivalTime);
            }
            return Integer.compare(p1.inputIndex, p2.inputIndex);
        });

        List<GanttBlock> gantt = new ArrayList<>();
        int currentTime = 0;

        for (Process p : procs) {
            if (currentTime < p.arrivalTime) {
                gantt.add(new GanttBlock("Idle", currentTime, p.arrivalTime));
                currentTime = p.arrivalTime;
            }
            p.firstStartTime = currentTime;
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            p.responseTime = p.firstStartTime - p.arrivalTime;
            p.completed = true;

            gantt.add(new GanttBlock(p.id, currentTime, p.completionTime));
            currentTime = p.completionTime;
        }

        procs.sort(Comparator.comparingInt(p -> p.completionTime));
        return new SimulationResult("First-Come, First-Served (FCFS)", 0, procs, mergeGanttBlocks(gantt));
    }

    public static SimulationResult runSJF(List<Process> inputList) {
        List<Process> procs = deepCopy(inputList);
        List<GanttBlock> gantt = new ArrayList<>();
        int currentTime = 0;
        int completedCount = 0;
        int n = procs.size();

        while (completedCount < n) {
            List<Process> ready = new ArrayList<>();
            for (Process p : procs) {
                if (!p.completed && p.arrivalTime <= currentTime) {
                    ready.add(p);
                }
            }

            if (ready.isEmpty()) {
                int nextArrival = Integer.MAX_VALUE;
                for (Process p : procs) {
                    if (!p.completed) {
                        nextArrival = Math.min(nextArrival, p.arrivalTime);
                    }
                }
                gantt.add(new GanttBlock("Idle", currentTime, nextArrival));
                currentTime = nextArrival;
                continue;
            }

            ready.sort((p1, p2) -> {
                if (p1.burstTime != p2.burstTime) {
                    return Integer.compare(p1.burstTime, p2.burstTime);
                }
                if (p1.arrivalTime != p2.arrivalTime) {
                    return Integer.compare(p1.arrivalTime, p2.arrivalTime);
                }
                return Integer.compare(p1.inputIndex, p2.inputIndex);
            });

            Process p = ready.get(0);
            p.firstStartTime = currentTime;
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
            p.responseTime = p.firstStartTime - p.arrivalTime;
            p.completed = true;
            completedCount++;

            gantt.add(new GanttBlock(p.id, currentTime, p.completionTime));
            currentTime = p.completionTime;
        }

        procs.sort(Comparator.comparingInt(p -> p.completionTime));
        return new SimulationResult("Shortest Job First (SJF)", 0, procs, mergeGanttBlocks(gantt));
    }

    public static SimulationResult runSRTF(List<Process> inputList) {
        List<Process> procs = deepCopy(inputList);
        List<GanttBlock> gantt = new ArrayList<>();
        int currentTime = 0;
        int completedCount = 0;
        int n = procs.size();

        GanttBlock currentBlock = null;

        while (completedCount < n) {
            Process shortest = null;
            int minRemaining = Integer.MAX_VALUE;

            for (Process p : procs) {
                if (!p.completed && p.arrivalTime <= currentTime) {
                    if (p.remainingTime < minRemaining) {
                        minRemaining = p.remainingTime;
                        shortest = p;
                    } else if (p.remainingTime == minRemaining && shortest != null) {
                        if (p.arrivalTime < shortest.arrivalTime) {
                            shortest = p;
                        } else if (p.arrivalTime == shortest.arrivalTime && p.inputIndex < shortest.inputIndex) {
                            shortest = p;
                        }
                    }
                }
            }

            if (shortest == null) {
                if (currentBlock != null && currentBlock.processId.equals("Idle")) {
                    currentBlock.endTime++;
                } else {
                    currentBlock = new GanttBlock("Idle", currentTime, currentTime + 1);
                    gantt.add(currentBlock);
                }
                currentTime++;
                continue;
            }

            if (shortest.firstStartTime == -1) {
                shortest.firstStartTime = currentTime;
            }

            if (currentBlock != null && currentBlock.processId.equals(shortest.id)) {
                currentBlock.endTime++;
            } else {
                currentBlock = new GanttBlock(shortest.id, currentTime, currentTime + 1);
                gantt.add(currentBlock);
            }

            shortest.remainingTime--;
            currentTime++;

            if (shortest.remainingTime == 0) {
                shortest.completed = true;
                completedCount++;
                shortest.completionTime = currentTime;
                shortest.turnaroundTime = shortest.completionTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
                shortest.responseTime = shortest.firstStartTime - shortest.arrivalTime;
            }
        }

        procs.sort(Comparator.comparingInt(p -> p.completionTime));
        return new SimulationResult("Shortest Remaining Time First (SRTF)", 0, procs, mergeGanttBlocks(gantt));
    }

    public static SimulationResult runRoundRobin(List<Process> inputList, int tq) {
        if (tq <= 0) tq = 1;
        List<Process> procs = deepCopy(inputList);

        procs.sort((p1, p2) -> {
            if (p1.arrivalTime != p2.arrivalTime) {
                return Integer.compare(p1.arrivalTime, p2.arrivalTime);
            }
            return Integer.compare(p1.inputIndex, p2.inputIndex);
        });

        List<GanttBlock> gantt = new ArrayList<>();
        Queue<Process> readyQueue = new LinkedList<>();
        Set<String> inQueue = new HashSet<>();

        int currentTime = 0;
        int completedCount = 0;
        int n = procs.size();
        int arrivalIdx = 0;

        while (arrivalIdx < n && procs.get(arrivalIdx).arrivalTime <= currentTime) {
            readyQueue.add(procs.get(arrivalIdx));
            inQueue.add(procs.get(arrivalIdx).id);
            arrivalIdx++;
        }

        while (completedCount < n) {
            if (readyQueue.isEmpty()) {
                if (arrivalIdx < n) {
                    int nextArrival = procs.get(arrivalIdx).arrivalTime;
                    gantt.add(new GanttBlock("Idle", currentTime, nextArrival));
                    currentTime = nextArrival;
                    while (arrivalIdx < n && procs.get(arrivalIdx).arrivalTime <= currentTime) {
                        readyQueue.add(procs.get(arrivalIdx));
                        inQueue.add(procs.get(arrivalIdx).id);
                        arrivalIdx++;
                    }
                } else {
                    break;
                }
            }

            Process p = readyQueue.poll();
            inQueue.remove(p.id);

            if (p.firstStartTime == -1) {
                p.firstStartTime = currentTime;
            }

            int execTime = Math.min(p.remainingTime, tq);
            gantt.add(new GanttBlock(p.id, currentTime, currentTime + execTime));
            currentTime += execTime;
            p.remainingTime -= execTime;

            while (arrivalIdx < n && procs.get(arrivalIdx).arrivalTime <= currentTime) {
                Process nextP = procs.get(arrivalIdx);
                if (!inQueue.contains(nextP.id) && !nextP.completed) {
                    readyQueue.add(nextP);
                    inQueue.add(nextP.id);
                }
                arrivalIdx++;
            }

            if (p.remainingTime > 0) {
                readyQueue.add(p);
                inQueue.add(p.id);
            } else {
                p.completed = true;
                completedCount++;
                p.completionTime = currentTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
                p.responseTime = p.firstStartTime - p.arrivalTime;
            }
        }

        procs.sort(Comparator.comparingInt(p -> p.completionTime));
        return new SimulationResult("Round Robin (RR)", tq, procs, mergeGanttBlocks(gantt));
    }

    private static List<Process> deepCopy(List<Process> list) {
        List<Process> copy = new ArrayList<>();
        for (Process p : list) {
            copy.add(p.copy());
        }
        return copy;
    }

    private static List<GanttBlock> mergeGanttBlocks(List<GanttBlock> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        List<GanttBlock> merged = new ArrayList<>();
        GanttBlock current = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            GanttBlock next = list.get(i);
            if (current.processId.equals(next.processId) && current.endTime == next.startTime) {
                current = new GanttBlock(current.processId, current.startTime, next.endTime);
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }
}
