import { Injectable } from '@angular/core';
import Chart, { ChartType, ChartConfiguration } from 'chart.js/auto';

export interface ChartDataset {
  label: string;
  data: any[];
  color?: string;
  fill?: boolean;
  borderDash?: number[];
  stack?: string;
}

export interface DiagramConfig {
  canvasId: string;
  type: ChartType | 'bar_vertical' | 'bar_horizontal' | 'semi_circle' | 'doughnut_ring' | 'pie_chart' | 'stacked_bar' | 'scatter_plot' | 'bubble_chart' | 'radar_chart' | 'polar_area';
  title: string;
  labels?: string[];
  datasets: ChartDataset[];
  unit?: string;
  xAxisLabel?: string;
  yAxisLabel?: string;
  maxScale?: number; // Permet de définir une valeur max (ex: 20 pour les notes)
}

@Injectable({ providedIn: 'root' })
export class DiagramService {

  private defaultColors = [
    'rgba(43, 108, 176, 0.8)',   // brand-blue-500
    'rgba(237, 137, 54, 0.8)',   // brand-orange-500
    'rgba(44, 82, 130, 0.8)',    // brand-blue-700
    'rgba(221, 107, 32, 0.8)',   // brand-orange-600
    'rgba(26, 54, 93, 0.8)',     // brand-blue-900
    'rgba(190, 227, 248, 0.8)',  // brand-blue-100
    'rgba(254, 235, 200, 0.8)',  // brand-orange-100
    'rgba(100, 116, 139, 0.8)'   // Slate neutre
  ];

  createChart(config: DiagramConfig): Chart | null {
    const ctx = document.getElementById(config.canvasId) as HTMLCanvasElement;
    if (!ctx) {
      console.error(`Canvas avec ID '${config.canvasId}' non trouvé.`);
      return null;
    }

    const chartType = this.mapType(config.type);
    const options = this.getBaseOptions(config);

    if (config.type === 'bar_horizontal') {
      (options as any).indexAxis = 'y';
    }

    if (config.type === 'semi_circle') {
      (options as any).circumference = 180;
      (options as any).rotation = -90;
    }

    if (config.type === 'stacked_bar') {
      options.scales.x.stacked = true;
      options.scales.y.stacked = true;
    }

    const chartConfig: ChartConfiguration = {
      type: chartType,
      data: {
        labels: config.labels,
        datasets: config.datasets.map((ds, index) => ({
          label: ds.label,
          data: ds.data,
          backgroundColor: this.getBackground(config.type, ds.color, index),
          borderColor: ds.color || this.defaultColors[index % this.defaultColors.length].replace('0.8', '1'),
          borderWidth: chartType === 'radar' ? 3 : 2,
          fill: ds.fill ?? (chartType === 'radar' || chartType === 'scatter' ? false : true),
          borderDash: ds.borderDash || [],
          tension: 0.3,
          stack: ds.stack
        }))
      },
      options: options
    };

    try {
      const existingChart = Chart.getChart(config.canvasId);
      if (existingChart) existingChart.destroy();

      return new Chart(ctx, chartConfig);
    } catch (error) {
      console.error('Diagram_Forge_Error:', error);
      return null;
    }
  }

  private mapType(type: string): ChartType {
    switch (type) {
      case 'bar_vertical':
      case 'bar_horizontal':
      case 'stacked_bar': return 'bar';
      case 'semi_circle':
      case 'pie_chart': return 'pie';
      case 'doughnut_ring': return 'doughnut';
      case 'radar_chart': return 'radar';
      case 'polar_area': return 'polarArea';
      case 'scatter_plot': return 'scatter';
      case 'bubble_chart': return 'bubble';
      default: return type as ChartType;
    }
  }

  private getBackground(type: string, color: string | undefined, index: number) {
    const multiColorTypes = ['pie', 'doughnut', 'polarArea', 'pie_chart', 'doughnut_ring', 'polar_area', 'semi_circle'];
    if (multiColorTypes.includes(type)) {
      return this.defaultColors;
    }
    return color || this.defaultColors[index % this.defaultColors.length];
  }

  private getBaseOptions(config: DiagramConfig): any {
    const chartType = this.mapType(config.type);
    const isHorizontal = config.type === 'bar_horizontal';
    const isRadial = ['radar', 'polarArea'].includes(chartType);
    const isPieDoughnut = ['pie', 'doughnut'].includes(chartType);
    const unit = config.unit ? ` ${config.unit}` : '';

    const options: any = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: config.title,
          color: '#1a365d',
          font: { size: 15, weight: '700', family: 'sans-serif' }
        },
        legend: {
          position: (isPieDoughnut || isRadial) ? 'right' : 'top',
          labels: {
            boxWidth: 12,
            color: '#2c5282',
            font: { size: 11, weight: '500' }
          }
        },
        tooltip: {
          backgroundColor: '#1a365d',
          titleColor: '#fffaf0',
          bodyColor: '#feebc8',
          padding: 10,
          callbacks: {
            label: (context: any) => {
              const label = context.dataset.label || '';
              // Récupération dynamique selon si le graphique est horizontal (x) ou vertical (y)
              let val = context.parsed;
              if (typeof context.parsed === 'object' && context.parsed !== null) {
                val = isHorizontal ? context.parsed.x : (context.parsed.y ?? context.parsed.r);
              }
              const formattedVal = val !== undefined ? Number(val).toLocaleString() : '0';
              return ` ${label}: ${formattedVal}${unit}`;
            }
          }
        }
      }
    };

    if (isRadial) {
      options.scales = {
        r: {
          angleLines: { color: '#bee3f8' },
          grid: { color: '#ebf8ff' },
          suggestedMin: 0,
          ticks: { backdropColor: 'transparent', color: '#2c5282', font: { size: 9 } }
        }
      };
    } else if (!isPieDoughnut) {
      // Configuration adaptative des axes X et Y selon l'orientation
      const valueAxis: any = {
        beginAtZero: true,
        grid: { color: '#f1f5f9' },
        ticks: {
          color: '#475569',
          font: { size: 10 },
          callback: (val: any) => val.toLocaleString() + unit
        }
      };

      if (config.maxScale !== undefined) {
        valueAxis.max = config.maxScale;
      }

      const categoryAxis: any = {
        grid: { display: false },
        ticks: { color: '#475569', font: { size: 10 } }
      };

      if (isHorizontal) {
        // En horizontal : X = Valeurs (Notes), Y = Catégories (Matières)
        valueAxis.title = { display: !!config.xAxisLabel, text: config.xAxisLabel, color: '#2c5282' };
        categoryAxis.title = { display: !!config.yAxisLabel, text: config.yAxisLabel, color: '#2c5282' };

        options.scales = {
          x: valueAxis,
          y: categoryAxis
        };
      } else {
        // En vertical : Y = Valeurs, X = Catégories
        valueAxis.title = { display: !!config.yAxisLabel, text: config.yAxisLabel, color: '#2c5282' };
        categoryAxis.title = { display: !!config.xAxisLabel, text: config.xAxisLabel, color: '#2c5282' };

        options.scales = {
          y: valueAxis,
          x: categoryAxis
        };
      }
    }

    return options;
  }
}
